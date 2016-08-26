/**
 * Created by Niranjan on 26-Aug-16.
 */
module.exports = angular.module('app.services')
    .service('LoadAttendeesService', function ($q, $http, APP_CONFIG, Helpers) {
        return {
            loadAttendees: function (eventId) {
                var deferred = $q.defer();
                var promise = Helpers.getPromise(deferred);
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
