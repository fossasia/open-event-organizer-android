function getPromise(deferred) {
    var promise = deferred.promise;
    promise.success = function (fn) {
        promise.then(fn);
        return promise;
    };
    promise.error = function (fn) {
        promise.then(null, fn);
        return promise;
    };
    return promise
}

module.exports = angular.module('app.services', [])
    .factory('verifyToken', ['$location', '$q', 'jwtHelper', function ($location, $q, jwtHelper) {
        var deferred = $q.defer();
        var promise = getPromise(deferred);
        var accessToken = localStorage.getItem('access_token');
        if (!accessToken || jwtHelper.isTokenExpired(accessToken)) {
            deferred.reject();
            $location.path("/login").replace();
        } else {
            deferred.resolve('valid');
        }
        return promise;
    }])
    .service('LoginService', function ($q, $http, APP_CONFIG, $localStorage) {
        return {
            loginUser: function (email, password) {
                var deferred = $q.defer();
                var promise = getPromise(deferred);
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
    }).service('LoadEventsService', function ($q, $http, APP_CONFIG) {
        return {
            loadEvents: function () {
                var deferred = $q.defer();
                var promise = getPromise(deferred);
                $http.get(APP_CONFIG.API_ENDPOINT + "/users/me/events")
                    .then(function successCallback(response) {
                        deferred.resolve(response.data);
                    }, function errorCallback(response) {
                        console.log(response);
                        if (response.status == 401) {
                            deferred.reject('auth_failed');
                        } else {
                            deferred.reject('Could not connect to the server.');
                        }
                    });
                return promise;
            }
        }
    }).service('LoadEventService', function ($q, $http, APP_CONFIG) {
        return {
            loadEvent: function (eventId) {
                var deferred = $q.defer();
                var promise = getPromise(deferred);
                $http.get(APP_CONFIG.API_ENDPOINT + "/events/" + eventId + "?include=tickets")
                    .then(function successCallback(response) {
                        deferred.resolve(response.data);
                    }, function errorCallback(response) {
                        console.log(response);
                        if (response.status == 401) {
                            deferred.reject('auth_failed');
                        } else {
                            deferred.reject('Could not connect to the server.');
                        }
                    });
                return promise;
            }
        }
    }).service('LoadAttendeesService', function ($q, $http, APP_CONFIG) {
        return {
            loadAttendees: function (eventId) {
                var deferred = $q.defer();
                var promise = getPromise(deferred);
                $http.get(APP_CONFIG.API_ENDPOINT + "/events/" + eventId + "/attendees/")
                    .then(function successCallback(response) {
                        deferred.resolve(response.data);
                    }, function errorCallback(response) {
                        console.log(response);
                        if (response.status == 401) {
                            deferred.reject('auth_failed');
                        } else {
                            deferred.reject('Could not connect to the server.');
                        }
                    });
                return promise;
            }
        }
    });
