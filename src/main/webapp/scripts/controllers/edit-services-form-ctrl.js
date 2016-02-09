'use strict';

/**
 * @ngdoc function
 * @name panelApp.controller:EditServicesFormCtrl
 * @description
 * # EditServicesFormCtrl
 * Controller of the panelApp
 */
angular.module('panelApp')
  .config(function($httpProvider) {
    // Configure all POST request as urlencoded
    $httpProvider.defaults.headers.post = {
      'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
    };
  })
  .controller('EditServicesFormCtrl', function($scope, $timeout, editServicesService) {

    $scope.checkServices = checkServices;
    $scope.toggleService = toggleService;
    $scope.submitEditServicesForm = submitEditServicesForm;
    $scope.resetEditServicesForm = resetEditServicesForm;
    $scope.wishList = [];
    $scope.editServicesMessage = null;
    $scope.editServicesFormSubmitted = false;

    function checkServices(terminal, service) {
      var terminalServicesLength = terminal.services.length;

      for (var i = 0; i < terminalServicesLength; i++) {
        if (terminal.services[i] === service.service) {
          return true;
        }
      }
      return false;
    }

    function toggleService(event, currentTerminal) {
      var toggledService = event.target.attributes['data-service'].value;
      var state = event.target.checked;
      var currentChange = {};
      var confirmed = false;

      currentChange.terminal = currentTerminal;
      currentChange.service = toggledService;
      currentChange.serviceName = $scope.getFullServiceName(toggledService);
      currentChange.state = state;

      var confirmed = confirm('Czy na pewno chcesz zmienić usługę ' + currentChange.serviceName + ' na terminalu ' + currentTerminal.name + '?');

      if (confirmed) {
        $scope.wishList.push(currentChange);
        buildMessage($scope.wishList);
      } else {
        event.preventDefault();
        return;
      }
    }

    function buildMessage(wishList) {
      var msg = '',
        stateName;

      var len = wishList.length;

      if (wishList[len - 1].state === true) {
        stateName = 'uruchomić';
      } else {
        stateName = 'wyłączyć';
      }

      msg = '• Chcę ' + stateName + ' usługę ' + wishList[len - 1].serviceName + ' na terminalu ' + wishList[len - 1].terminal.number + '\n';

      if (document.getElementById('editServicesMessage') !== null) {
        document.getElementById('editServicesMessage').value += msg;
      } else {
        document.getElementById('editServicesMessage').value = msg;
      }

      $scope.editServicesMessage = document.getElementById('editServicesMessage').value;
    }

    function submitEditServicesForm(editServicesMessage) {
      var message = {
        issue: 'CHANGE-SERVICE',
        message: editServicesMessage
      };

      editServicesService(message).then(function success(resp) {

        if (resp.status === 200) {
          $scope.editServicesFormSubmitted = true;

          $timeout(function() {
            $scope.hideInfo();
            $scope.resetEditServicesForm();
            $scope.editServicesFormSubmitted = false;
          }, 3000)
        }
      }, function error(resp) {
        console.log(resp);
      });
    }

    function resetEditServicesForm() {
      $scope.hideInfo();
      $scope.editServicesMessage = null;
    }

  });
