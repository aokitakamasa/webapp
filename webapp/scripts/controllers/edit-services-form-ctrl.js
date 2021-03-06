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
  .controller('EditServicesFormCtrl', function($scope, $timeout, restService, editServicesService) {

    var vm = this;
    vm.terminals = null;
    vm.services = null;
    vm.currentEditTerminal = {
      services: []
    };
    vm.editServicesMessage = null;
    vm.editServicesFormSubmitted = false;
    vm.checkServices = checkServices;
    vm.toggleService = toggleService;
    vm.submitEditServicesForm = submitEditServicesForm;
    vm.resetEditServicesForm = resetEditServicesForm;
    vm.setCurrentEditTerminalByNumber = setCurrentEditTerminalByNumber;
    vm.wishList = [];

    restService.then(function success(resp) {
      vm.terminals = resp.data.terminals;
      vm.services = resp.data.services;
    }, function error(resp) {
      console.log(resp.data);
    });

    function checkServices(terminal, service) {

      if(!terminal.services[0]) {
        return;
      }

      var terminalServicesLength = terminal.services.length;

      for (var i = 0; i < terminalServicesLength; i++) {
        if (terminal.services[i] === service.service) {
          return true;
        }
      }
      return false;
    }

    function setCurrentEditTerminalByNumber(terminalNumber) {
      for (var i = 0; i < vm.terminals.length; i++) {
        if (vm.terminals[i].number === terminalNumber) {
          vm.currentEditTerminal = vm.terminals[i];
        }
      }
      $scope.panel.scrollTo('detailsContainer');
    }

    function toggleService(event, currentTerminal) {
      var toggledService = event.target.attributes['data-service'].value;
      var state = event.target.checked;
      var currentChange = {};
      var confirmed = false;

      currentChange.terminal = currentTerminal;
      currentChange.service = toggledService;
      currentChange.serviceName = $scope.panel.getFullServiceName(toggledService);
      currentChange.state = state;

      var confirmed = confirm('Czy na pewno chcesz zmienić usługę ' + currentChange.serviceName + ' na terminalu ' + currentTerminal.name + '?');

      if (confirmed) {
        vm.wishList.push(currentChange);
        buildMessage(vm.wishList);
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

      vm.editServicesMessage = document.getElementById('editServicesMessage').value;
    }

    function submitEditServicesForm(editServicesMessage) {
      var message = {
        issue: 'CHANGE-SERVICE',
        message: editServicesMessage
      };

      editServicesService(message).then(function success(resp) {

        if (resp.status === 200) {
          vm.editServicesFormSubmitted = true;

          $timeout(function() {
            vm.hideInfo();
            vm.resetEditServicesForm();
            vm.editServicesFormSubmitted = false;
          }, 3000);
        }
      }, function error(resp) {
        console.log(resp);
      });
    }

    function resetEditServicesForm() {
      $scope.panel.hideInfo();
      vm.editServicesMessage = null;
    }

  });
