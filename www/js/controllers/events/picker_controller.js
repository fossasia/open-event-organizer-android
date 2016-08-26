/**
 * Created by Niranjan on 26-Aug-16.
 */
module.exports = angular.module('app.controllers')
    .controller('eventPickerCtrl', function ($scope, $stateParams, $ionicNavBarDelegate, $location, LoadEventsService, $localStorage, $state) {
        $scope.events = [];
        $scope.isLoading = true;
        $scope.$storage = $localStorage;
        $scope.pickEvent = function (event) {
            $scope.$storage.selectedEvent = event;
            $state.go("event_dashboard", {}, {location: 'replace'});
        };

        LoadEventsService
            .loadEvents()
            .success(function (data) {
                $scope.events = data;
                $scope.isLoading = false;
            })
            .error(function (data) {
                if(data === 'auth_failed') {
                    $state.go("login", {}, {location: 'replace'});
                } else {
                    $ionicPopup.alert({
                        title: 'Data loading failed',
                        template: data
                    });
                    $scope.isLoading = false;
                }
            });
    });
