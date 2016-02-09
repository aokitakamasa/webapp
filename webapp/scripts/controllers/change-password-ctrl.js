'use strict';

/**
 * @ngdoc function
 * @name panelApp.controller:ChangePasswordCtrl
 * @description
 * # ChangePasswordCtrl
 * Controller of the panelApp
 */
angular.module('panelApp')
  .controller('ChangePasswordCtrl', function($scope, $timeout, changePasswordService) {

    $scope.changePasswordFormSubmit = changePasswordFormSubmit;
    $scope.resetForm = resetForm;
    $scope.passwordChanged = false;
    $scope.passwordIncorrect = false;
    $scope.passwordConfig = {
      'minLength': 4,
      'minCapital': 1,
      'minNumbers': 1,
      'minSpecial': 1
    };

    var userPassword = {
      currentPassword: null,
      newPassword: null,
      confirmPassword: null
    };

    function changePasswordFormSubmit(userPassword) {
      var passwordObj = {
        currentPassword: userPassword.currentPassword,
        newPassword: userPassword.confirmPassword
      };

      changePasswordService(passwordObj).then(function success(resp) {
        console.log(resp);
        if (resp.status === 204) {
          $scope.passwordChanged = true;

          $timeout(function() {
            $(document.querySelector('#cahngePasswordModal')).modal('hide');
            $scope.resetForm();
            $scope.passwordChanged = false;
          }, 3000);
        }
      }, function error(resp) {
        if (resp.status === 403) {
          $scope.passwordIncorrect = true;
          $timeout(function() {
            $scope.resetForm();
            $scope.passwordIncorrect = false;
          }, 3000);
        }
        console.log(resp.data);
      });
    }

    function resetForm() {
      $scope.userPassword = angular.copy(userPassword);
      $scope.changePasswordForm.$setPristine();
    }

  });
