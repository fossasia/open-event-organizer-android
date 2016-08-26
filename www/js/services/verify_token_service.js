/**
 * Created by Niranjan on 26-Aug-16.
 */
module.exports = angular.module('app.services')
    .factory('verifyToken', function ($location, $q, jwtHelper, $state, Helpers) {
        var deferred = $q.defer();
        var promise = Helpers.getPromise(deferred);
        var accessToken = localStorage.getItem('access_token');
        if (!accessToken || jwtHelper.isTokenExpired(accessToken)) {
            deferred.reject();
            $state.go("login", {}, {location: 'replace'});
        } else {
            deferred.resolve('valid');
        }
        return promise;
    });
