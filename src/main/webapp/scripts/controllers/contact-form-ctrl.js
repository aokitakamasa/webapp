'use strict';

/**
 * @ngdoc function
 * @name panelApp.controller:ContactFormCtrl
 * @description
 * # ContactFormCtrl
 * Controller of the panelApp
 */
angular.module('panelApp')
  .controller('ContactFormCtrl', function($scope, $timeout, contactFormService) {

    $scope.contactFormSubmitted = false;

    var master = {
      issue: 'ISSUE',
      userId: $scope.merchant.id,
      email: $scope.merchant.email,
      phone: $scope.merchant.phone
    };

    $scope.contactFormData = angular.copy(master);

    $scope.submitContactForm = submitContactForm;
    $scope.resetContactForm = resetContactForm;

    function submitContactForm(data) {
      contactFormService(data).then(function success(resp) {
        if (resp.status === 200) {
          $scope.contactFormSubmitted = true;
          $timeout(function() {
            $scope.hideInfo();
            $scope.resetContactForm();
            $scope.contactFormSubmitted = false;
          }, 3000);
        }
      }, function error(resp) {
        console.log(resp);
      });
    }

    function resetContactForm() {
      $scope.contactFormData = angular.copy(master);
    }

  });
