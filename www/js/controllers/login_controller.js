/**
 * Created by Niranjan on 26-Aug-16.
 */
module.exports = angular.module('app.controllers')
    .controller('loginCtrl', function ($scope, $stateParams, $http, LoginService, $ionicPopup, $localStorage, verifyToken, $state) {
        $scope.user = {};
        verifyToken.then(function () {
            if($localStorage.selectedEvent) {
                $state.go("event_dashboard", {}, {location: 'replace'});
            }
        });
        $scope.login = function () {
            $scope.isLoading = true;
            LoginService
                .loginUser($scope.user.email, $scope.user.password)
                .success(function (data) {
                    console.log(data);
                    $state.go("event_picker", {}, {location: 'replace'});
                })
                .error(function (data) {
                    $ionicPopup.alert({
                        title: 'Login failed',
                        template: data
                    });
                    $scope.isLoading = false;
                });
        }
    });
