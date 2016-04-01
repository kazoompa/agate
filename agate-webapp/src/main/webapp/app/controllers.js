'use strict';

/* Controllers */

agate.controller('MainController', ['$rootScope', '$scope', '$log', 'ConfigurationResource', 'screenSize', 'AuthenticationSharedService',
  function ($rootScope, $scope, $log, ConfigurationResource, screenSize, AuthenticationSharedService) {
    $rootScope.screen = $scope.screen = {size: null, device: null};
    if (AuthenticationSharedService.isAuthenticated()) {
      $scope.agateConfig = ConfigurationResource.get();
    }
    $rootScope.$on('event:auth-loginConfirmed', function () {
      $scope.agateConfig = ConfigurationResource.get();
    });

    function getScreenSize() {
      var size = ['lg', 'md', 'sm', 'xs'].filter(function (size) {
        return screenSize.is(size);
      });

      $scope.screen.size = size ? size[0] : 'lg';
      $scope.screen.device = screenSize.is('md, lg') ? 'desktop' : 'mobile';
      $scope.screen.is = screenSize.is;

      $log.debug('Screen', $scope.screen);
    }

    getScreenSize();

    screenSize.on('lg, md, sm, xs', function () {
      getScreenSize();
    });
  }]);

agate.controller('AdminController', [function () {}]);

agate.controller('LanguageController', ['$scope', '$translate',
  function ($scope, $translate) {
    $scope.changeLanguage = function (languageKey) {
      $translate.use(languageKey);
    };
  }]);

agate.controller('MenuController', [function () {}]);

agate.controller('LoginController', ['$scope', '$location', 'AuthenticationSharedService',
  function ($scope, $location, AuthenticationSharedService) {
    $scope.login = function () {
      AuthenticationSharedService.login({
        username: $scope.username,
        password: $scope.password,
        success: function () {
          $location.path('');
        }
      });
    };
  }]);

agate.controller('JoinController', ['$rootScope', '$scope', '$location', 'JoinConfigResource', 'JoinResource', 'ClientConfig',
  'NOTIFICATION_EVENTS', 'ServerErrorUtils', 'AlertService', 'vcRecaptchaService',
  function ($rootScope, $scope, $location, JoinConfigResource, JoinResource, ClientConfig, NOTIFICATION_EVENTS, ServerErrorUtils, AlertService, vcRecaptchaService) {

    $scope.joinConfig = JoinConfigResource.get();
    $scope.model = {};
    $scope.response = null;
    $scope.widgetId = null;
    $scope.config = ClientConfig;

    $scope.setResponse = function (response) {
      $scope.response = response;
    };

    $scope.setWidgetId = function (widgetId) {
      $scope.widgetId = widgetId;
    };

    $scope.onSubmit = function (form) {
      // First we broadcast an event so all fields validate themselves
      $scope.$broadcast('schemaFormValidate');

      if (!$scope.response) {
        AlertService.alert({id: 'JoinController', type: 'danger', msgKey: 'missing-reCaptcha-error'});
        return;
      }

      if (form.$valid) {
        JoinResource.post(angular.extend({}, $scope.model, {reCaptchaResponse: $scope.response}))
          .success(function () {
            $location.url($location.path());
            $location.path('/');
          })
          .error(function (data) {
            $rootScope.$broadcast(NOTIFICATION_EVENTS.showNotificationDialog, {
              message: ServerErrorUtils.buildMessage(data)
            });

            vcRecaptchaService.reload($scope.widgetId);
          });
      }
    };

  }]);

agate.controller('LogoutController', ['$location', 'AuthenticationSharedService',
  function ($location, AuthenticationSharedService) {
    AuthenticationSharedService.logout({
      success: function () {
        $location.path('');
      }
    });
  }]);

agate.controller('OAuthController', ['$scope', '$q', '$location', 'ApplicationSummaryResource', function($scope, $q, $location, ApplicationSummaryResource) {
  $scope.auth = $location.search();
  $scope.client = ApplicationSummaryResource.get({ id: $scope.auth.client_id },function(){
  }, function(){
    $scope.error = 'unknown-client-application';
    $scope.errorArgs = $scope.auth.client_id;
  });

  $scope.scopes = $scope.auth.scope.split(' ').map(function (s) {
    var scopeParts = s.split(':'), appId = scopeParts[0];
    return {application: appId, name: scopeParts[1] };
  });

  var applications = $scope.scopes.reduce(function(applications, scope) {
    if(applications.indexOf(scope.application) < 0) {
      applications.push(scope.application);
    }

    return applications;
  }, []);

  $q.all(applications.map(function(application) {
    if (application !== 'openid') {
      return ApplicationSummaryResource.get({id: application}).$promise;
    } else {
      var deferred = $q.defer();
      deferred.resolve({id: 'openid', scopes: []});
      return deferred.promise;
    }
  })).then(function(applications) {
    var res = $scope.scopes.map(function(scope) {
      var application = applications.filter(function(application) { return application.id === scope.application; })[0],
        found = application.scopes.filter(function(s) {return s.name === scope.name; })[0];

      if (!found && scope.name) {
          scope.isMissing = true;
      } else {
        scope.description = found ? found.description : null;
      }

      return scope;
    });

    var missingScopes = res.filter(function(scope) { return scope.isMissing; });

    if(missingScopes.length > 0) {
      $scope.error = 'unknown-resource-scope';
      $scope.errorArgs = missingScopes.map(function(s) { return s.application + ':' + s.name; }).join(', ');
    }

    $scope.applicationScopes = applications.map(function(application) {
      var scopes = res.filter(function(scope) { return scope.application === application.id; });
      return {application: application, scopes: scopes};
    });
  }).catch(function() {
    $scope.error = 'unknown-resource-application';
    $scope.errorArgs = applications.join(', ');
  });
}]);

agate.controller('ProfileController', ['$scope', '$location', '$uibModal', 'Account', 'AccountAuthorizations', 'AccountAuthorization', 'ConfigurationResource', 'AttributesService', 'AlertService',
  function ($scope, $location, $uibModal, Account, AccountAuthorizations, AccountAuthorization, ConfigurationResource, AttributesService, AlertService) {
    var getConfigAttributes = function () {
      ConfigurationResource.get(function (config) {
        $scope.attributesConfig = config.userAttributes || [];
        $scope.attributeConfigPairs = AttributesService.getAttributeConfigPairs($scope.settingsAccount.attributes, $scope.attributesConfig);
        $scope.usedAttributeNames = AttributesService.getUsedAttributeNames($scope.settingsAccount.attributes, $scope.attributesConfig);
      });
    };

    var getSettingsAccount = function () {
      $scope.settingsAccount = Account.get(function (user) {
        $scope.authorizations = AccountAuthorizations.query();
        ConfigurationResource.get(function (config) {
          $scope.userConfigAttributes = AttributesService.findConfigAttributes(user.attributes, config.userAttributes);
          $scope.userNonConfigAttributes = config.userAttributes ? AttributesService.findNonConfigAttributes(user.attributes, config.userAttributes) : user.attributes;
          $scope.usedAttributeNames = AttributesService.getUsedAttributeNames($scope.settingsAccount.attributes, $scope.attributesConfig);
        });

        return user;
      });
    }

    getConfigAttributes();
    getSettingsAccount();

    $scope.deleteAuthorization = function (authz) {
      AccountAuthorization.delete({authz: authz.id}, function () {
        $scope.authorizations = AccountAuthorizations.query();
      });
    };

    $scope.onPasswordUpdated = function () {
      AlertService.alert({id: 'ProfileController', type: 'success', msgKey: 'profile.success.updated', delay: 5000});
    };

    $scope.cancel = function () {
      $location.path('/profile');
    };

    $scope.edit = function () {
      var settingsAccountClone = $.extend(true, {}, $scope.settingsAccount);
      var attributeConfigPairs =
        AttributesService.getAttributeConfigPairs(settingsAccountClone.attributes, $scope.attributesConfig);


      $uibModal
        .open({
          templateUrl: 'app/views/profile/profile-form-modal.html',
          controller: 'ProfileModalController',
          resolve: {
            settingsAccount: function () {
              return settingsAccountClone;
            },
            attributeConfigPairs: function () {
              return attributeConfigPairs;
            },
            attributesConfig: function () {
              return $scope.attributesConfig;
            },
            usedAttributeNames: function () {
              return $scope.usedAttributeNames;
            }
          }
        })
        .result.then(function () {
          AlertService.alert({
            id: 'ProfileController',
            type: 'success',
            msgKey: 'profile.success.updated',
            delay: 5000
          });
          getSettingsAccount();
        }, function () {
        });
    };


  }]);

agate.controller('ProfileModalController', ['$scope', '$uibModalInstance', '$filter', 'Account', 'settingsAccount', 'attributeConfigPairs', 'attributesConfig', 'usedAttributeNames', 'AttributesService', 'AlertService',
  function ($scope, $uibModalInstance, $filter, Account, settingsAccount, attributeConfigPairs, attributesConfig, usedAttributeNames, AttributesService, AlertService) {
    $scope.settingsAccount = settingsAccount;
    $scope.attributeConfigPairs = attributeConfigPairs;
    $scope.attributesConfig = attributesConfig;
    $scope.usedAttributeNames = usedAttributeNames;

    $scope.requiredField = $filter('translate')('user.email');

    $scope.cancel = function () {
      $uibModalInstance.dismiss('cancel');
    };

    $scope.save = function (form) {
      if (!form.$valid) {
        form.saveAttempted = true;
        AlertService.alert({id: 'ProfileModalController', type: 'danger', msgKey: 'fix-error'});
        return;
      }

      form.$pristine = true;
      $scope.settingsAccount.attributes =
        AttributesService.mergeConfigPairAttributes($scope.settingsAccount.attributes, $scope.attributeConfigPairs);

      Account.save($scope.settingsAccount,
        function () {
          $uibModalInstance.close($scope.attributeConfigPairs);
        },
        function () {
          AlertService.alert({id: 'ProfileModalController', type: 'danger', msgKey: 'fix-error'});
        });
    };
  }]);

agate.controller('ResetPasswordController', ['$scope', '$location', 'ConfirmResource', 'PasswordResetResource',
  function ($scope, $location, ConfirmResource, PasswordResetResource) {
    var isReset = $location.path() === '/reset_password' ? true : false;

    $scope.title = isReset ? 'Reset password' : 'Confirm';
    $scope.key = $location.search().key;

    $scope.changePassword = function () {
      var resource = isReset ? PasswordResetResource : ConfirmResource;

      if ($scope.password !== $scope.confirmPassword) {
        $scope.doNotMatch = 'ERROR';
      } else {
        $scope.doNotMatch = null;
        resource.post({
          key: $scope.key,
          password: $scope.password
        })
          .success(function () {
            $location.url($location.path());
            $location.path('/');
          });
      }
    };
  }]);

agate.controller('ForgotLoginDetailsController', ['$scope', 'AlertService', 'ForgotPasswordResource',
  function ($scope, AlertService, ForgotPasswordResource) {
    $scope.username = '';

    $scope.sendRequest = function(form) {
      if (!form.$valid) {
        form.saveAttempted = true;
        AlertService.alert({id: 'ForgotLoginDetailsController', type: 'danger', msgKey: 'fix-error', delay: 5000});
        return;
      }

      var successHandler = function() {
        AlertService.alert({id: 'ForgotLoginDetailsController', type: 'success', msgKey: 'forgot-login.success'});
        $scope.email = '';
        $scope.username = '';
        form.$setPristine();
      };

      var errorHandler = function() {
        AlertService.alert({id: 'ForgotLoginDetailsController', type: 'danger', msgKey: 'error', delay: 5000});
      };

      ForgotPasswordResource.post({username: $scope.username}).success(successHandler).error(errorHandler);
    };
  }]);
