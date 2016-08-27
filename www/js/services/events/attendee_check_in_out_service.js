/**
 * Created by Niranjan on 26-Aug-16.
 */
module.exports = angular.module('app.services')
    .service('AttendeeCheckInOutService', function ($q, $http, APP_CONFIG, Helpers) {
        return {
            checkInOut: function (eventId, attendeeId, isCheckedIn) {
                var deferred = $q.defer();
                var promise = Helpers.getPromise(deferred);

                var endpoint = APP_CONFIG.API_ENDPOINT + "/events/" + eventId + "/attendees/check_in_toggle/" + attendeeId + "/";
                if(isCheckedIn) {
                    endpoint += "check_in";
                } else {
                    endpoint += "check_out";
                }

                $http.post(endpoint, {})
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
