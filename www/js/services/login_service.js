/**
 * Created by Niranjan on 26-Aug-16.
 */
module.exports = angular.module('app.services')
    .service('LoginService', function ($q, $http, APP_CONFIG, $localStorage, Helpers) {
        return {
            loginUser: function (email, password) {
                var deferred = $q.defer();
                var promise = Helpers.getPromise(deferred);
                $http.post(APP_CONFIG.API_ENDPOINT + "/login", {
                    email: email,
                    password: password
                }).then(function successCallback(loginResponse) {
                    if (loginResponse.data.hasOwnProperty('access_token')) {
                        localStorage.setItem('access_token', loginResponse.data.access_token);
                        $http.get(APP_CONFIG.API_ENDPOINT + "/users/me")
                            .then(function successCallback(userResponse) {
                                $localStorage.user = userResponse.data;
                                deferred.resolve(loginResponse.data);
                            }, function errorCallback(userResponse) {
                                console.log(userResponse);
                                if (userResponse.status == 401) {
                                    deferred.reject('Credentials were incorrect.');
                                } else {
                                    deferred.reject('Could not connect to the server.');
                                }
                            });
                    } else {
                        deferred.reject('An unexpected error occurred.');
                    }
                }, function errorCallback(loginResponse) {
                    console.log(loginResponse);
                    if (loginResponse.status == 401) {
                        deferred.reject('Credentials were incorrect.');
                    } else {
                        deferred.reject('Could not connect to the server.');
                    }
                });
                return promise;
            }
        }
    });
