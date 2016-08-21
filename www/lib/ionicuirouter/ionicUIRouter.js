function checkState(check, state){
    var setTo = check;
    var states = state.get();
    var foundAlternative = false;
    var currentState = state.current;

    if (!currentState.views){
        return setTo;
    }

    var checkView = setTo+'_'+Object.keys(currentState.views)[0];

    for (var i=0;i<states.length;i++){
        if (states[i].name == setTo){
            return setTo;
        }
        if (states[i].name == checkView){
            foundAlternative = true;
        }
    }

    if (foundAlternative){
        return checkView;
    }else{
        return setTo;
    }
}

angular.module('ionicUIRouter', [])

.config([
  '$provide', '$stateProvider',
function($provide, $stateProvider) {

  function $StateDecorator($state) {

    $state.__go = $state.go;

    $state.go = function(to, params, options){
        to = checkState(to, $state);
        return $state.__go(to, params, options);
    }

    return $state;

  }

  $provide.decorator('$state', ['$delegate', $StateDecorator]);

  $stateProvider.__state = $stateProvider.state;

    $stateProvider.state = function(statename, data){
        
        if (data.views){

            var numKeys = 0;
            for (var key in data.views){
                numKeys++;
                if (numKeys > 1){
                    for (var key in data.views){
                        var copy = {};
                        angular.copy(data, copy);
                        copy.views = {};
                        copy.views[key] = data.views[key];
                        copy.url = '/'+key+data.url;
                        $stateProvider.__state(statename+'_'+key, copy);
                    }
                    return $stateProvider;
                }
            }
        }

        $stateProvider.__state(statename, data);

        return $stateProvider;
  }

}])

.directive('uiSref', function($state){
    return {
        restrict: 'A',
        priority: -1,
        link: function(scope, element, attrs){
            attrs.uiSref = checkState(attrs.uiSref, $state);
        }
    }
})

;