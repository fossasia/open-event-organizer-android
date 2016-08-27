/**
 * Created by Niranjan on 26-Aug-16.
 */

function groupByAlphabets($filter, data) {
    var attendees = $filter('orderBy')(data, 'lastname');
    var attendeesGrouped = {};
    for (var i = 0; i < attendees.length; i++) {
        var letter = attendees[i].lastname.charAt(0).toUpperCase();
        if (attendeesGrouped[letter] == undefined) {
            attendeesGrouped[letter] = []
        }
        attendeesGrouped[letter].push(attendees[i]);
    }
    return attendeesGrouped;
}

module.exports = angular.module('app.controllers')
    .controller('eventAttendeesCtrl', function ($scope, $stateParams, $ionicNavBarDelegate, $location, LoadEventsService, $localStorage, verifyToken, LoadAttendeesService, $filter, AttendeeCheckInOutService) {
        $scope.isLoading = true;
        var attendees = [];
        $scope.$storage = $localStorage;
        $scope.attendeesGrouped = [];

        $scope.checkInToggle = function (attendee) {
            attendee.checked_in = !attendee.checked_in;
            AttendeeCheckInOutService.checkInOut($localStorage.selectedEvent.id, attendee.id, attendee.checked_in)
                .success(function (data) {

                })
                .error(function (data) {
                    attendee.checked_in = !attendee.checked_in;
                    if (data === 'auth_failed') {
                        $state.go("login", {}, {location: 'replace'});
                    } else {
                        $ionicPopup.alert({
                            title: 'An error occurred',
                            template: data
                        });
                    }
                });
        };

        $scope.filter = function (query) {
            $scope.attendeesGrouped = groupByAlphabets($filter, $filter('filter')(attendees, query));
        };

        verifyToken.then(function () {
            LoadAttendeesService
                .loadAttendees($localStorage.selectedEvent.id)
                .success(function (data) {
                    attendees = data;
                    $scope.attendeesGrouped = groupByAlphabets($filter, data);
                    $scope.isLoading = false;
                })
                .error(function (data) {
                    $scope.isLoading = false;
                    if (data === 'auth_failed') {
                        $state.go("login", {}, {location: 'replace'});
                    } else {

                    }
                });
        });
    });
