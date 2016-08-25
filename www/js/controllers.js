// The following are the constructor functions for each page's controller. See https://docs.angularjs.org/guide/controller
// You can include any angular dependencies as parameters for this function
// TIP: Access Route Parameters for your page via $stateParams.parameterName


module.exports = angular.module('app.controllers', [])
    .controller('loginCtrl', function ($scope, $stateParams, $http, LoginService, $ionicPopup, $location) {
        $scope.user = {};
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
        $ionicNavBarDelegate.showBackButton(false);
        $scope.events = [];
        $scope.isLoading = true;
        $scope.$storage = $localStorage;
        verifyToken.then(function () {
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
        });
    })
