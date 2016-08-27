/**
 * Created by Niranjan on 26-Aug-16.
 */
module.exports = angular.module('app.controllers')
    .controller('eventAttendeesCtrl', function ($scope, $stateParams, $ionicNavBarDelegate, $location, LoadEventsService, $localStorage, verifyToken, LoadAttendeesService) {
        $scope.isLoading = true;
        $scope.$storage = $localStorage;
        $scope.attendees = [];
        $scope.checkIn = function (attendee) {

        };

        verifyToken.then(function () {
            LoadAttendeesService
                .loadAttendees($localStorage.selectedEvent.id)
                .success(function (data) {
                    $scope.attendees = data;
                    $scope.isLoading = false;
                })
                .error(function (data) {
                    $scope.isLoading = false;
                    if(data === 'auth_failed') {
                        $state.go("login", {}, {location: 'replace'});
                    } else {

                    }
                });
        });
    });
