'use strict';

angular
  .module('forgotPasswordApp', ['ngAnimate', 'ngMessages'])
  .config(function($httpProvider) {
    // Configure all POST request as urlencoded
    $httpProvider.defaults.headers.post = {
      'Content-Type': 'application/x-www-form-urlencoded'
    };
    // Configure all PUT request as urlencoded
    // $httpProvider.defaults.headers.put = {
    //   'Content-Type': 'application/x-www-form-urlencoded'
    // };
    $httpProvider.defaults.transformRequest = function(obj) {
      var str = [];
      for (var p in obj) {
        str.push(encodeURIComponent(p) + '=' + encodeURIComponent(obj[p]));
      }
      return str.join('&');
    };
  })
  .controller('ForgotPasswordCtrl', function($scope, $timeout, forgotPasswordService) {

    $scope.loading = false;
    $scope.sendSuccess = false;
    $scope.errorStatus = null;
    $scope.forgotPasswordFormSubmit = forgotPasswordFormSubmit;
    $scope.resetForm = resetForm;
    $scope.userData = {};

    function forgotPasswordFormSubmit(userData) {
      $scope.loading = true;

      forgotPasswordService(userData).then(function success(resp) {
        console.log(resp);
        if (resp.status === 200) {
          $scope.sendSuccess = true;
          $scope.loading = false;
          $timeout(function() {
            window.location.replace('/panel/');
          }, 3000);
        }
      }, function error(resp) {
        console.log(resp);

        switch (resp.status) {
          case 400:
            $scope.errorStatus = resp.status;
            break;
          case 404:
            $scope.errorStatus = resp.status;
            break;
          case 500:
            $scope.errorStatus = resp.status;
            break;
          default:
            $scope.errorStatus = 'error';
            break;
        }

        $scope.loading = false;

        $timeout(function() {
          $scope.userData = {};
          $scope.errorStatus = null;
        }, 5000);
      });
    }

    function resetForm() {
      $scope.userData = {};
      window.location.replace('/panel/');
    }
  })
  .service('forgotPasswordService', function($http) {
    return function(userData) {
      return $http.post('/panel/api/user/forgotPassword ', userData);
    };
  });
