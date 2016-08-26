/**
 * Created by Niranjan on 26-Aug-16.
 */
module.exports = angular.module('app.controllers')
    .controller('eventDashboardCtrl', function ($scope, $stateParams, $ionicNavBarDelegate, $location, LoadEventsService, $localStorage, verifyToken, LoadEventService, LoadAttendeesService, $state) {
        $scope.isLoading = false;
        $scope.$storage = $localStorage;
        $scope.stats = {
            tickets: {
                sold: 0,
                total: 0
            },
            attendees: {
                total: 0,
                present: 0
            }
        };

        $scope.percentage = function(value, total){
            if (total <= 0) {
                return 0;
            }
            var result = ((value/total)*100);
            return Math.round(result);
        };

        verifyToken.then(function () {
            LoadEventService
                .loadEvent($localStorage.selectedEvent.id)
                .success(function (data) {
                    $localStorage.selectedEvent = data;
                    angular.forEach(data.tickets, function(ticket) {
                        $scope.stats.tickets.total += ticket.quantity;
                    });
                })
                .error(function (data) {
                    if(data === 'auth_failed') {
                        $state.go("login", {}, {location: 'replace'});
                    } else {

                    }
                });
            LoadAttendeesService
                .loadAttendees($localStorage.selectedEvent.id)
                .success(function (data) {
                    $scope.stats.attendees.total = $scope.stats.tickets.sold = data.length;
                    angular.forEach(data, function(attendee) {
                        $scope.stats.tickets.total += (attendee.checked_in ? 1 : 0);
                    });
                })
                .error(function (data) {
                    if(data === 'auth_failed') {
                        $state.go("login", {}, {location: 'replace'});
                    } else {

                    }
                });
        });
    });
