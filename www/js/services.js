module.exports = angular.module('app.services', [])
    .factory('verifyToken', ['$location', '$q', '$localStorage', 'jwtHelper', function ($location, $q, $localStorage, jwtHelper) {
        var defer = $q.defer();
        var accessToken = $localStorage.access_token;
        if (accessToken === null || jwtHelper.isTokenExpired(accessToken)) {
            defer.reject();
            $location.path("/login").replace();
        } else {
            defer.resolve('valid');
        }
        return defer.promise;
    }])
    .service('LoginService', function ($q, $http, APP_CONFIG, $localStorage) {
        return {
            loginUser: function (email, password) {
                var deferred = $q.defer();
                var promise = deferred.promise;

                $http.post(APP_CONFIG.API_ENDPOINT + "/login", {
                    email: email,
                    password: password
                }).then(function successCallback(response) {
                    if (response.data.hasOwnProperty('access_token')) {
                        $localStorage.access_token = response.data.access_token;
                        deferred.resolve(response.data);
                    } else {
                        deferred.reject('An unexpected error occurred.');
                    }
                }, function errorCallback(response) {
                    console.log(response);
                    if (response.status == 401) {
                        deferred.reject('Credentials were incorrect.');
                    } else {
                        deferred.reject('Could not connect to the server.');
                    }
                });
                promise.success = function(fn) {
                    promise.then(fn);
                    return promise;
                };
                promise.error = function(fn) {
                    promise.then(null, fn);
                    return promise;
                };
                return promise;
            }
        }
    });
