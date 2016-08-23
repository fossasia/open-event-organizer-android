// The following are the constructor functions for each page's controller. See https://docs.angularjs.org/guide/controller
// You can include any angular dependencies as parameters for this function
// TIP: Access Route Parameters for your page via $stateParams.parameterName


module.exports = angular.module('app.controllers', [])
    .controller('loginCtrl', ['$scope', '$stateParams', '$http', 'LoginService', '$ionicPopup', '$location',
        function ($scope, $stateParams, $http, LoginService, $ionicPopup, $location) {

        $scope.user = {

        };
        $scope.login = function() {
            $scope.isLoading = true;
            LoginService.loginUser($scope.user.email, $scope.user.password).success(function(data) {
                console.log(data);
                $location.path("/");
            }).error(function(data) {
                $ionicPopup.alert({
                    title: 'Login failed',
                    template: data
                });
                $scope.isLoading = false;
            });
        }
    }])
    .controller('dashboardCtrl', ['$scope', '$stateParams', '$ionicNavBarDelegate', function ($scope, $stateParams, $ionicNavBarDelegate) {
        $ionicNavBarDelegate.showBackButton(false);
    }]);
