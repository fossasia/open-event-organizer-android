// The following are the constructor functions for each page's controller. See https://docs.angularjs.org/guide/controller
// You can include any angular dependencies as parameters for this function
// TIP: Access Route Parameters for your page via $stateParams.parameterName


module.exports = angular.module('app.controllers', [])
    .controller('loginCtrl', function ($scope, $stateParams, $http, LoginService, $ionicPopup, $location, $localStorage, verifyToken) {
        $scope.user = {};
        verifyToken.then(function () {
            if($localStorage.selectedEvent) {
                $location.path("/event").replace();
            } else {
                $location.path("/").replace();
            }
        });
        $scope.login = function () {
            $scope.isLoading = true;
            LoginService
                .loginUser($scope.user.email, $scope.user.password)
                .success(function (data) {
                    console.log(data);
                    $location.path("/").replace();
                })
                .error(function (data) {
                    $ionicPopup.alert({
                        title: 'Login failed',
                        template: data
                    });
                    $scope.isLoading = false;
                });
        }
    })
    .controller('eventPickerCtrl', function ($scope, $stateParams, $ionicNavBarDelegate, $location, LoadEventsService, $localStorage, verifyToken) {
        $scope.events = [];
        $scope.isLoading = true;
        $scope.$storage = $localStorage;
        $scope.pickEvent = function (event) {
            $scope.$storage.selectedEvent = event;
            $location.path("/event").replace();
        };

        LoadEventsService
            .loadEvents()
            .success(function (data) {
                $scope.events = data;
                $scope.isLoading = false;
            })
            .error(function (data) {
                if(data === 'auth_failed') {
                    $location.path("/login").replace();
                } else {
                    $ionicPopup.alert({
                        title: 'Data loading failed',
                        template: data
                    });
                    $scope.isLoading = false;
                }
            });
    })
    .controller('eventDashboardCtrl', function ($scope, $stateParams, $ionicNavBarDelegate, $location, LoadEventsService, $localStorage, verifyToken, LoadEventService, LoadAttendeesService) {
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
                        $location.path("/login").replace();
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
                        $location.path("/login").replace();
                    } else {

                    }
                });
        });
    });
