/*! validator-web 1.0.0
 * (c) 2016-2017 Youqian Yue <devefx@163.com>, Apache Licensed
 * https://github.com/devefx/validator-web
 */
;(function(factory) {
    if ('function' === typeof define && (define.amd || define.cmd)) {
        // Register as an anonymous module.
        define([], function(){
            return factory;
        });
    } else {
        factory(jQuery);
    }
}(function($) {
    $.extend($.fn, {
        validate: function(options) {
            // if nothing is selected, return nothing; can't chain anyway
            if (!this.length) {
                if (options && options.debug && window.console) {
                    console.warn("Nothing selected, can't validate, returning nothing.");
                }
                return;
            }

            // check if a validator for this form was already created
            var validator = $.data(this[0], "validator");
            if (validator) {
                return validator;
            }

            // Add novalidate tag if HTML5.
            this.attr("novalidate", "novalidate");

            validator = new $.validator(options, this[0]);
            $.data(this[0], "validator", validator);

            if (validator.settings.onsubmit) {

                this.on("click.validate", ":submit", function(event) {
                    if (validator.settings.submitHandler) {
                        validator.submitButton = event.target;
                    }

                    // allow suppressing validation by adding a cancel class to the submit button
                    if ($(this).hasClass("cancel")) {
                        validator.cancelSubmit = true;
                    }
                    
                    // allow suppressing validation by adding the html5 formnovalidate attribute to the submit button
                    if ($(this).attr("formnovalidate") !== undefined) {
                        validator.cancelSubmit = true;
                    }
                });

                // validate the form on submit
                this.on("submit.validate", function(event) {
                    if (validator.settings.debug) {
                        // prevent form submit to be able to see console output
                        event.preventDefault();
                    }
                    function handle() {
                        var hidden, result;
                        if (validator.settings.submitHandler) {
                            if (validator.submitButton) {
                                // insert a hidden input as a replacement for the missing submit button
                                hidden = $("<input type='hidden'/>")
                                    .attr("name", validator.submitButton.name)
                                    .val($(validator.submitButton).val())
                                    .appendTo(validator.currentForm);
                            }
                            result = validator.settings.submitHandler.call(validator, validator.currentForm, event);
                            if (validator.submitButton) {
                                // and clean up afterwards; thanks to no-block-scope, hidden can be referenced
                                hidden.remove();
                            }
                            if (result !== true) {
                                return result || false;
                            }
                        }
                        // jquery ajaxform plugin
                        var ajaxsubmit = validator.settings.ajaxsubmit;
                        if (ajaxsubmit) {
                            var options = $.extend({}, ajaxsubmit);
                            options.data = $.extend(options.data || {}, {
                                _validator_ajaxsubmit: 'true'
                            });
                            options.success = function (res) {
                                if (res && res.status == 101) {
                                    for (var name in res.contents) {
                                        validator.invalid[name] = true;
                                        if (typeof(ajaxsubmit.invalid) == 'function') {
                                            ajaxsubmit.invalid(name);
                                        }
                                    }
                                    validator.showErrors(res.contents);
                                    return;
                                }
                                typeof(ajaxsubmit.success) == 'function' && ajaxsubmit.success(res);
                            };
                            $(validator.currentForm).ajaxSubmit(options);
                            return false;
                        }
                        return true;
                    }

                    // prevent submit for invalid forms or custom submit handlers
                    if (validator.cancelSubmit) {
                        validator.cancelSubmit = false;
                        return handle();
                    }
                    if (validator.form()) {
                        if (validator.pendingRequest) {
                            validator.formSubmitted = true;
                            return false;
                        }
                        return handle();
                    } else {
                        validator.focusInvalid();
                        return false;
                    }
                });

            }

            return validator;
        },

        valid: function () {
            var valid, validator, errorList;

            if ($(this[0]).is("form")) {
                valid = this.validate().form();
            } else {
                errorList = [];
                validator = $(this[0].form).validate();
                this.each(function () {
                    validator.element(this);
                    errorList = errorList.concat(validator.errorList);
                });
                validator.errorList = errorList;
                valid = validator.valid();
            }
            return valid;
        }

    });

    $(function () {
        $.each($("form[valid]"), function (n, form) {
            var params = $(form).attr("valid");
            $(form).validate(params ? eval("(" + params + ")") : {});
        });
    });
}));

$.validationContext = function() {
    this.constraints = [];
    this.failFast = false;
    this.throwException = false;
    this.setFailFast = function (failFast) {
        this.failFast = failFast;
    };
    this.setThrowException = function (throwException) {
        this.throwException = throwException;
    };
    this.constraint = function () {
        if (arguments.length < 3) {
            throw new Error("invalid arguments");
        }
        var args = $.makeArray(arguments);
        var descriptor = {
            name: args[0],
            message: args[1],
            constraintValidator: args[2],
            groups: (arguments.length > 3 ? args.slice(3) : ["Default"])
        };
        this.constraints.push(descriptor);
    };
};

$.parseURL = function (url) {
    url = url.replace(/\s/g, "");
    var a = document.createElement("a");
    a.href = url;
    if (url.indexOf(a.protocol) != 0) {
        throw new Error("no protocol: " + url);
    }
    return {
        source: url,
        protocol: a.protocol.replace(":", ""),
        host: a.hostname,
        port: a.port || -1,
        query: a.search.substring(1),
        params: (function () {
            var ret = {},
                seg = a.search.replace(/^\?/, '').split('&'),
                len = seg.length, i = 0, s;
            for (; i < len; i++) {
                if (!seg[i]) {
                    continue;
                }
                s = seg[i].split('=');
                ret[s[0]] = s[1];
            }
            return ret;
        })(),
        file: (a.pathname.match(/\/([^\/?#]+)$/i) || [, ''])[1],
        hash: a.hash.replace('#', ''),
        path: a.pathname.replace(/^([^\/])/, '/$1'),
        relative: (a.href.match(/tps?:\/\/[^\/]+(.+)/) || [, ''])[1],
        segments: a.pathname.replace(/^\//, '').split('/')
    };
};

$.validator = function(options, form) {
    this.settings = $.extend(true, {}, $.validator.defaults, options);
    this.currentForm = form;
    this.init();
};

$.validator.format = function(source, params) {
    if (arguments.length === 1) {
        return function(params) {
            var args = $.makeArray(arguments);
            args.unshift(source);
            return $.validator.format.apply(this, args);
        };
    }

    var valueExpression = {
        variables: [],
        setVariable: function (name, value) {
            this.variables.push(name + "=params."+ name);
        },
        getValue: function (expression) {
            eval("var " + this.variables.join(",") + ";");
            return eval(expression);
        }
    };

    $.each(params, function(k, v) {
        valueExpression.setVariable(k, v);
        source = source.replace(new RegExp("\\{" + k + "\\}", "g"), function() {
            return v;
        });
    });

    var patt = new RegExp("\\${(.+?)}", "g");
    var result, str = source;
    while ((result = patt.exec(source))) {
        var expression = result[1],
            resolvedExpression = valueExpression.getValue(expression);
        str = str.replace(result[0], resolvedExpression);
    }
    return str;
};

$.extend($.validator, {
    
    defaults: {
        directly: false,
        failFast: false,
        throwException: false,
        groups: ["Default"],
        errorClass: "error",
        validClass: "valid",
        errorElement: "label",
        focusCleanup: false,
        focusInvalid: true,
        errorContainer: $([]),
        errorLabelContainer: $([]),
        onsubmit: true,
        ajaxsubmit: {},
        ignore: ":hidden",
        languageParameterName: "_language",

        onfocusin: function(element) {
            this.lastActive = element;

            // Hide error label and remove error class on focus if enabled
            if (this.settings.focusCleanup) {
                if (this.settings.unhighlight) {
                    this.settings.unhighlight.call(this, element, this.settings.errorClass, "");
                }
                this.hideThese(this.errorsFor(element));
            }
        },

        onfocusout: function(element) {
            if (!this.checkable(element) && (this.settings.directly || element.name in this.submitted || $(element).val().length)) {
                this.element(element);
            }
        },

        onkeyup: function(element, event) {
            // Avoid revalidate the field when pressing one of the following keys
            // Shift       => 16
            // Ctrl        => 17
            // Alt         => 18
            // Caps lock   => 20
            // End         => 35
            // Home        => 36
            // Left arrow  => 37
            // Up arrow    => 38
            // Right arrow => 39
            // Down arrow  => 40
            // Insert      => 45
            // Num lock    => 144
            // AltGr key   => 225
            var excludedKeys = [
                16, 17, 18, 20, 35, 36, 37,
                38, 39, 40, 45, 144, 225
            ];

            if (event.which === 9 && this.elementValue(element) === "" || $.inArray(event.keyCode, excludedKeys) !== -1) {
                return;
            } else if (element.name in this.submitted || element === this.lastElement) {
                this.element(element);
            }
        },

        onclick: function(element) {
            // click on selects, radiobuttons and checkboxes
            if (element.name in this.submitted) {
                this.element(element);

            // or option elements, check parent select in that case
            } else if (element.parentNode.name in this.submitted) {
                this.element(element.parentNode);
            }
        },

        onchange: function (element) {
            if (element.type == "file") {
                $(element).removeData("savedValue").removeData("previousValue");
                this.element(element);
            }
        },

        highlight: function(element, errorClass, validClass) {
            if (element.type === "radio") {
                this.findByName(element.name).addClass(errorClass).removeClass(validClass);
            } else {
                $(element).addClass(errorClass).removeClass(validClass);
            }
        },

        unhighlight: function(element, errorClass, validClass) {
            if (element.type === "radio") {
                this.findByName(element.name).removeClass(errorClass).addClass(validClass);
            } else {
                $(element).removeClass(errorClass).addClass(validClass);
            }
        }
    },
    
    setDefaults: function(settings) {
        $.extend($.validator.defaults, settings);
    },
    
    prototype: {
        init: function() {
            this.labelContainer = $(this.settings.errorLabelContainer);
            this.errorContext = this.labelContainer.length && this.labelContainer || $(this.currentForm);
            this.containers = $(this.settings.errorContainer).add(this.settings.errorLabelContainer);
            this.submitted = {};
            this.pendingRequest = 0;
            this.pending = {};
            this.invalid = {};
            this.reset();
            this.uncheckedErrorList = [];
            this.context = new $.validationContext();
            this.initContext();
            this.initLanguage();
            this.checkElements();

            function delegate(event) {
                var validator = $.data(this.form, "validator"),
                    eventType = "on" + event.type.replace(/^validate/, ""),
                    settings = validator.settings;
                if (settings[eventType] && !$(this).is(settings.ignore)) {
                    settings[eventType].call(validator, this, event);
                }
            }

            $(this.currentForm)
                .on("focusin.validate focusout.validate keyup.validate",
                    ":text, [type='password'], select, textarea, [type='number'], [type='search'], " +
                    "[type='tel'], [type='url'], [type='email'], [type='datetime'], [type='date'], [type='month'], " +
                    "[type='week'], [type='time'], [type='datetime-local'], [type='range'], [type='color'], " +
                    "[type='radio'], [type='checkbox']", delegate)
                // Support: Chrome, oldIE
                // "select" is provided as event.target when clicking a option
                .on("click.validate", "select, option, [type='radio'], [type='checkbox']", delegate)
                .on("change.validate", "[type=file]", delegate);

            if (this.settings.invalidHandler) {
                $(this.currentForm).on("invalid-form.validate", this.settings.invalidHandler);
            }
        },

        initContext: function () {
            this.context.failFast = this.settings.failFast;
            this.context.throwException = this.settings.throwException;
            var use = this.settings.use;
            if (use) {
                var validation = $.validator.validations[use];
                if (validation) {
                    validation(this.context);
                } else {
                    throw new Error("not found validation '" + use + "', please check if there is any.");
                }
            } else {
                var results = $.map($.validator.validations, function(v, k) {
                    return {id: k, validation: v};
                });

                if (results.length) {
                    if (results.length > 1) {
                        throw new Error("the validation is not the only, please specify the name of the validation.");
                    }
                    var result = results[0];
                    this.settings.use = result.id;
                    result.validation(this.context);
                } else {
                    throw new Error("cannot find any validation.");
                }
            }
        },
        
        initLanguage: function () {
            if (this.settings.language &&
                this.settings.languageParameterName) {
                // find language element
                var language = this.findByName(this.settings.languageParameterName);
                if (language.length) {
                    language.val(this.settings.language);
                } else {
                    // create language element
                    language = $('<input type="hidden">')
                        .attr("name", this.settings.languageParameterName)
                        .val(this.settings.language);
                    $(this.currentForm).append(language);
                }
            }
        },
        
        checkElements: function () {
            var validator = this;
            $.each(this.context.constraints, function (n, i) {
                var elements = validator.findByName(i.name);
                if (elements.length == 0 && (!i.name || !i.constraintValidator.isValid(undefined, validator))) {
                    validator.uncheckedErrorList.push({
                        element: elements[0],
                        message: i.message,
                        validator: i.constraintValidator
                    });
                    if (window.console) {
                        console.error("Form elements is missing, the name is '%s'.", i.name);
                    }
                }
            });
        },
        
        hasConstraint: function (element) {
            var element = this.validationTargetFor(this.clean(element));
            return $.map(this.context.constraints, function(n, i) {
                return n.name == element.name || undefined;
            }).length > 0;
        },

        groupsMatch: function (descriptor) {
            if (this.settings.groups.length == 0) {
                return $.inArray("Default", descriptor.groups) !== -1;
            }
            return $.map(this.settings.groups, function (n, i) {
                return $.inArray(n, descriptor.groups) !== -1 || undefined;
            }).length > 0;
        },

        findConstraints: function (element) {
            return $.map(this.context.constraints, function (n, i) {
                return (element.name == n.name && n) || undefined;
            });
        },

        form: function () {
            this.checkForm();
            $.extend(this.submitted, this.errorMap);
            this.invalid = $.extend({}, this.errorMap);
            if (!this.valid()) {
                $(this.currentForm).triggerHandler("invalid-form", [this]);
            }
            this.showErrors();
            return this.valid();
        },

        checkForm: function () {
            this.prepareForm();
            for(var i = 0, elements = (this.currentElements = this.elements()); elements[i]; i++) {
                if (this.check(elements[i]) === false && this.context.failFast) {
                    break;
                }
            }
            return this.valid();
        },

        element: function (element) {
            var cleanElement = this.clean(element),
                checkElement = this.validationTargetFor(cleanElement),
                result = true;

            this.lastElement = checkElement;

            if (checkElement === undefined) {
                delete this.invalid[cleanElement.name];
            } else {
                this.prepareElement(checkElement);
                this.currentElements = $(checkElement);

                result = this.check(checkElement) !== false;
                if (result) {
                    delete this.invalid[checkElement.name];
                } else {
                    this.invalid[checkElement.name] = true;
                }
            }
            // Add aria-invalid status for screen readers
            $(element).attr("aria-invalid", !result);

            if (!this.numberOfInvalids()) {
                // Hide error containers on last error
                this.toHide = this.toHide.add(this.containers);
            }
            this.showErrors();
            return result;
        },

        showErrors: function (errors) {
            if (errors) {
                // add items to error list and map
                $.extend(this.errorMap, errors);
                this.errorList = [];
                for (var name in errors) {
                    this.errorList.push({
                        message: errors[name],
                        element: this.findByName(name)[0]
                    });
                }
                // remove items from success list
                this.successList = $.grep(this.successList, function(element) {
                    return !(element.name in errors);
                });
            }
            if (this.settings.showErrors) {
                this.settings.showErrors.call(this, this.errorMap, this.errorList);
            } else {
                this.defaultShowErrors();
            }
        },

        resetForm: function() {
            if ($.fn.resetForm) {
                $(this.currentForm).resetForm();
            }
            this.submitted = {};
            this.lastElement = null;
            this.prepareForm();
            this.hideErrors();
            var i, elements = this.elements()
                .removeData("previousValue")
                .removeAttr("aria-invalid");

            if (this.settings.unhighlight) {
                for (i = 0; elements[i]; i++) {
                    this.settings.unhighlight.call(this, elements[i],
                        this.settings.errorClass, "");
                }
            } else {
                elements.removeClass(this.settings.errorClass);
            }
        },

        numberOfInvalids: function () {
            return this.objectLength(this.invalid);
        },

        objectLength: function(obj) {
            var count = 0, i;
            for (i in obj) {
                count++;
            }
            return count;
        },

        hideErrors: function() {
            this.hideThese(this.toHide);
        },

        hideThese: function(elements) {
            elements.not(this.containers).text("");
            this.addWrapper(elements).hide();
        },

        valid: function() {
            return this.errorList.length === 0 && this.uncheckedErrorList.length == 0;
        },

        focusInvalid: function() {
            if (this.settings.focusInvalid) {
                try {
                    $(this.findLastActive() || this.errorList.length && this.errorList[0].element || [])
                    .filter(":visible")
                    .focus()
                    // manually trigger focusin event; without it, focusin handler isn't called, findLastActive won't have anything to find
                    .trigger("focusin");
                } catch (e) {
                    // ignore IE throwing errors when focusing hidden elements
                }
            }
        },

        findLastActive: function() {
            var lastActive = this.lastActive;
            return lastActive && $.grep(this.errorList, function(n) {
                return n.element.name === lastActive.name;
            }).length === 1 && lastActive;
        },

        elements: function() {
            var validator = this,
                elesCache = {};
            
            return $(this.currentForm)
            .find("input, select, textarea")
            .not(":submit, :reset, :image, :disabled")
            .not(this.settings.ignore)
            .filter(function () {
                if (!this.name && validator.settings.debug && window.console) {
                    console.error("%o has no name assigned", this);
                }

                if (this.name in elesCache || !validator.hasConstraint($(this))) {
                    return false;
                }

                elesCache[this.name] = true;
                return true;
            });
        },

        clean: function (selector) {
            return $(selector)[0];
        },

        errorElements: function () {
            var errorClass = this.settings.errorClass.split(" ").join(".");
            return $(this.settings.errorElement + "." + errorClass, this.errorContext);
        },

        reset: function () {
            this.successList = [];
            this.errorList = [];
            this.errorMap = {};
            this.toShow = $([]);
            this.toHide = $([]);
            this.currentElements = $([]);
        },

        prepareForm: function () {
            this.reset();
            this.toHide = this.errorElements().add(this.containers);
        },

        prepareElement: function (element) {
            this.reset();
            this.toHide = this.errorsFor(element);
        },

        elementValue: function (element) {
            var val,
                $element = $(element),
                type = element.type;
            
            if (type === "radio" || type === "checkbox") {
                return this.findByName(element.name).filter(":checked").val();
            } else if (type === "number" && typeof element.validity !== "undefined") {
                return element.validity.badInput ? false : $element.val();
            } else if (type === "file") {
                var file = $element[0].files[0];
                if (this.imageable(file)) {
                    var savedValue = $.data(element, "savedValue");
                    return savedValue || function (callback) {
                        var reader = new FileReader();
                        reader.readAsDataURL(file);
                        reader.onload = function() {
                            var img = new Image();
                            img.src = reader.result;
                            img.onload = function () {
                                file.width = img.width;
                                file.height = img.height;
                                $.data(element, "savedValue", file);
                                if (callback) {
                                    callback(file);
                                }
                            };
                        };
                    };
                }
                return file;
            }

            val = $element.val();
            if (typeof val === "string") {
                return val.replace(/\r/g, "");
            }
            return val;
        },

        check: function (element) {
            element = this.validationTargetFor(this.clean(element));

            var constraints = this.findConstraints(element),
                value = this.elementValue(element),
                self = this, result, constraint, constraintValidator;
            
            if (typeof value == "function") {
                this.startRequest(element);
                value(function (val) {
                    self.stopRequest(element, self.element(element));
                });
                return;
            }

            for (var i in constraints) {
                constraint = constraints[i],
                    constraintValidator = constraint.constraintValidator;
                try {
                    result = !this.groupsMatch(constraint) || constraintValidator.isValid(value, this, element, constraint);
                    
                    if (result === "pending") {
                        this.toHide = this.toHide.not(this.errorsFor(element));
                        return;
                    }

                    if (!result) {
                        this.formatAndAdd(element, constraint);
                        return false;
                    }
                } catch (e) {
                    if (this.settings.debug && window.console) {
                        console.log("Exception occurred when checking element " + element.id + ", check the '" + constraintValidator.constructor.name + "' constraint.", e);
                    }
                    if (e instanceof TypeError) {
                        e.message += ".  Exception occurred when checking element " + element.id + ", check the '" + constraintValidator.constructor.name + "' constraint.";
                    }
                    if (this.context.throwException) {
                        throw e;
                    }
                    return false;
                }
            }
            if (this.hasConstraint(element)) {
                this.successList.push(element);
            }
            return true;
        },

        getMessage: function (element, constraint) {
            var message = constraint.message,
                theregex = /\$?\{([^{}]+)\}/g;
            if (typeof message === "function") {
                message = message.call(this, constraint.constraintValidator, element);
            } else if (theregex.test(message)) {
                var parameters = $.extend({
                    value: this.elementValue(element)
                }, constraint.constraintValidator);
                message = $.validator.format(message, parameters);
            }
            return message;
        },

        formatAndAdd: function (element, constraint) {
            var message = this.getMessage(element, constraint);

            this.errorList.push({
                message: message,
                element: element,
                validator: constraint.constraintValidator
            });
            this.errorMap[element.name] = message;
            this.submitted[element.name] = message;
        },

        addWrapper: function(toToggle) {
            if (this.settings.wrapper) {
                toToggle = toToggle.add(toToggle.parent(this.settings.wrapper));
            }
            return toToggle;
        },

        defaultShowErrors: function () {
            var i, elements, error;
            for (i = 0; this.errorList[i]; i++) {
                error = this.errorList[i];
                if (this.settings.highlight) {
                    this.settings.highlight.call(this, error.element, this.settings.errorClass, this.settings.validClass);
                }
                this.showLabel(error.element, error.message);
            }
            if (this.errorList.length) {
                this.toShow = this.toShow.add(this.containers);
            }
            if (this.settings.success) {
                for (i = 0; this.successList[i]; i++) {
                    this.showLabel(this.successList[i]);
                }
            }
            if (this.settings.unhighlight) {
                for (i = 0, elements = this.validElements(); elements[i]; i++) {
                    this.settings.unhighlight.call(this, elements[i], this.settings.errorClass, this.settings.validClass);
                }
            }
            this.toHide = this.toHide.not(this.toShow);
            this.hideErrors();
            this.addWrapper(this.toShow).show();
        },

        validElements: function() {
            return this.currentElements.not(this.invalidElements());
        },

        invalidElements: function() {
            return $(this.errorList).map(function() {
                return this.element;
            });
        },

        showLabel: function (element, message) {
            var place, errorId,
                error = this.errorsFor(element),
                elementID = this.idOrName(element),
                describedby = $(element).data("aria-describedby");

            if (error.length) {
                // refresh error/success class
                error.removeClass(this.settings.validClass).addClass(this.settings.errorClass);
                // replace message on existing label
                error.html(message);
            } else {
                // create error element
                error = $("<" + this.settings.errorElement + ">")
                    .attr("id", elementID + "-error")
                    .addClass(this.settings.errorClass)
                    .html(message || "");
                
                // Maintain reference to the element to be placed into the DOM
                place = error;
                if (this.settings.wrapper) {
                    // make sure the element is visible, even in IE
                    // actually showing the wrapped element is handled elsewhere
                    place = error.hide().show().wrap("<" + this.settings.wrapper + "/>").parent();
                }
                if (this.labelContainer.length) {
                    this.labelContainer.append(place);
                } else if (this.settings.errorPlacement) {
                    this.settings.errorPlacement(place, $(element));
                } else {
                    place.insertAfter(element);
                }

                // Link error back to the element
                if (error.is("label")) {
                    // If the error is a label, then associate using 'for'
                    error.attr("for", elementID);
                } else if (error.parents("label[for='" + elementID + "']").length === 0) {
                    // If the element is not a child of an associated label, then it's necessary
                    // to explicitly apply aria-describedby

                    errorID = error.attr("id").replace(/(:|\.|\[|\]|\$)/g, "\\$1");
                    // Respect existing non-error aria-describedby
                    if (!describedBy) {
                        describedBy = errorID;
                    } else if (!describedBy.match(new RegExp("\\b" + errorID + "\\b"))) {
                        // Add to end of list if not already present
                        describedBy += " " + errorID;
                    }
                    $(element).attr("aria-describedby", describedBy);
                }
            }
            if (message && this.settings.error) {
                if (typeof this.settings.error === "string") {
                    error.addClass(this.settings.error);
                } else {
                    this.settings.error(error, element);
                }
            }
            if (!message && this.settings.success) {
                error.text("");
                if (typeof this.settings.success === "string") {
                    error.addClass(this.settings.success);
                } else {
                    this.settings.success(error, element);
                }
            }
            this.toShow = this.toShow.add(error);
        },

        errorsFor: function (element) {
            var name = this.idOrName(element),
                describer = $(element).attr("aria-describedby"),
                selector = "label[for='" + name + "'], label[for='" + name + "'] *";
            
            // aria-describedby should directly reference the error element
            if (describer) {
                selector = selector + ", #" + describer.replace(/\s+/g, ", #");
            }
            return this
                .errorElements()
                .filter(selector);
        },

        idOrName: function (element) {
            return this.checkable(element) ? element.name : element.id || element.name;
        },

        validationTargetFor: function (element) {

            // If radio/checkbox, validate first element in group instead
            if (this.checkable(element)) {
                element = this.findByName(element.name);
            }

            // Always apply ignore filter
            return $(element).not(this.settings.ignore)[0];
        },

        checkable: function(element) {
            return (/radio|checkbox/i).test(element.type);
        },

        imageable: function (file) {
            return file && (/image\/(jpeg|png|gif)/).test(file.type);
        },

        findByName: function(name) {
            return $(this.currentForm).find("[name='" + name + "']");
        },

        startRequest: function(element) {
            if (!this.pending[element.name]) {
                this.pendingRequest++;
                this.pending[element.name] = true;
            }
        },

        stopRequest: function(element, valid) {
            this.pendingRequest--;
            // sometimes synchronization fails, make sure pendingRequest is never < 0
            if (this.pendingRequest < 0) {
                this.pendingRequest = 0;
            }
            delete this.pending[element.name];
            if (valid && this.pendingRequest === 0 && this.formSubmitted && this.form()) {
                $(this.currentForm).submit();
                this.formSubmitted = false;
            } else if (!valid && this.pendingRequest === 0 && this.formSubmitted) {
                $(this.currentForm).triggerHandler("invalid-form", [this]);
                this.formSubmitted = false;
            }
        },

        previousValue: function (element, url) {
            var previous = $.data(element, "previousValue") || $.data(element, "previousValue", {});
            if (!previous[url]) {
                previous[url] = {
                    old: null,
                    valid: false
                };
            }
            return previous[url];
        },

        // cleans up all forms and elements, removes validator-specific events
        destroy: function() {
            this.resetForm();

            $(this.currentForm)
                .off(".validate")
                .removeData("validator");
        }
    },

    validations: {},

});


/** extensions */
function SimpleDateFormat(pattern) {
    this.pattern = pattern;
    this.regex = new RegExp("^" + pattern.replace("yyyy", "\\d{4}").replace("MM", "(0\\d|1[0-2])").replace("dd", "([0-2]\\d|3[0-1])")
            .replace("HH", "([0-1]\\d|2[0-3])").replace("hh", "(0\\d|1[0-2])").replace("mm", "[0-5]\\d").replace("ss", "[0-5]\\d") + "$");
    this.position = {
        year: pattern.indexOf("yyyy"), month: pattern.indexOf("MM"), day: pattern.indexOf("dd"),
        hour: pattern.toLowerCase().indexOf("hh"), minute: pattern.indexOf("mm"), second: pattern.indexOf("ss")
    };
    this.parse = function (source) {
        if (!this.regex.test(source))
            throw new Error("Unparseable date: \"" + source + "\"");
        var time = {
            year: source.substr(this.position.year, 4),
            month: source.substr(this.position.month, 2),
            day: source.substr(this.position.day, 2)
        };
        if (this.position.hour != -1)
            time.hour = source.substr(this.position.hour, 2);
        if (this.position.minute != -1)
            time.minute = source.substr(this.position.minute, 2);
        if (this.position.second != -1)
            time.second = source.substr(this.position.second, 2);
        var day31 = "01,03,05,07,08,10,12";
        if (time.day == 31 && day31.indexOf(time.month) == -1)
            throw new Error("Unparseable date: \"" + source + "\"");
        if (time.month == 2 && time.day == 29 && !(time.year % 4 == 0 && time.year % 100 != 0)
            && !(time.year % 100 == 0 && time.year % 400 == 0)) {
            throw new Error("Unparseable date: \"" + source + "\"");
        }
        var date = new Date();
        date.setFullYear(time.year, time.month - 1, time.day);
        if (time.hour != undefined) date.setHours(time.hour);
        if (time.minute != undefined) date.setMinutes(time.minute);
        if (time.second != undefined) date.setSeconds(time.second);
        return date;
    };
    this.format = function (date) {
        function fmt(v, n) {
            for (var i = n - (v + "").length; i > 0; i--) {
                v = "0" + v;
            }
            return v;
        }
        var h24 = date.getHours();
        return this.pattern.replace("yyyy", fmt(date.getFullYear(), 4)).replace("MM", fmt(date.getMonth() + 1, 2))
            .replace("dd", fmt(date.getDate(), 2)).replace("HH", fmt(h24, 2)).replace("hh", fmt((h24 - 1) % 12 + 1, 2))
            .replace("mm", fmt(date.getMinutes(), 2)).replace("ss", fmt(date.getSeconds(), 2));
    };
}

Number.prototype.scale = function () {
    var numStr = this.toString();
    var scale = 0;
    var index = numStr.indexOf(".");
    if (index != -1) {
        scale = numStr.length - index - 1;
    } else {
        var matcher = /0*$/.exec(numStr);
        if (matcher.length != 0) {
            scale -= matcher[0].length;
        }
    }
    return scale;
};

Number.prototype.precision = function () {
    var numStr = this.toString();
    var precision = numStr.length;
    if (numStr.indexOf(".") != -1) {
        precision -= 1;
    } else {
        var matcher = /0*$/.exec(numStr);
        if (matcher.length != 0) {
            precision -= matcher[0].length;
        }
    }
    return precision;
};

/** constraints */
$.validator.constraints = {

    AssertFalse: function () {
        this.isValid = function (value) {
            return !value || value != "true";
        };
    },

    AssertTrue: function () {
        this.isValid = function (value) {
            return !value || value == "true"; 
        };
    },

    DecimalMax: function (maxValue, inclusive) {
        this.maxValue = maxValue;
        this.inclusive = inclusive || inclusive == undefined;
        this.isValid = function (value) {
            if (!value) {
                return true;
            }
            value = Number(value);
            if (isNaN(value)) {
                return false;
            }
            return this.inclusive ? value <= this.maxValue : value < this.maxValue;
        };
    },

    DecimalMin: function (minValue, inclusive) {
        this.minValue = minValue;
        this.inclusive = inclusive || inclusive == undefined;
        this.isValid = function (value) {
            if (!value) {
                return true;
            }
            value = Number(value);
            if (isNaN(value)) {
                return false;
            }
            return this.inclusive ? value >= this.minValue : value > this.minValue;
        };
    },

    Digits: function (maxIntegerLength, maxFractionLength) {
        this.maxIntegerLength = maxIntegerLength;
        this.maxFractionLength = maxFractionLength;
        this.isValid = function (value) {
            if (!value) {
                return true;
            }
            var bigNum = Number(value);
            if (isNaN(value)) {
                return false;
            }
            var integerPartLength = bigNum.precision() - bigNum.scale();
            var fractionPartLength = bigNum.scale() < 0 ? 0 : bigNum.scale();
            return (this.maxIntegerLength >= integerPartLength && this.maxFractionLength >= fractionPartLength);
        };
    },
    
    Email: function () {
        this.isValid = function (value) {
            if (!value || value.length == 0) {
                return true;
            }
            var splitPosition = value.lastIndexOf("@");
            if (splitPosition < 0) {
                return false;
            }
            var name = value.substring(0, splitPosition);
            var domain = value.substring(splitPosition + 1);
            var valid = false;
            if (domain == "163.com" || domain == "126.com" || domain == "yeah.net") {
                valid = valid || /^[a-z][a-z0-9_]{5,17}$/i.test(name);
            } else if (domain == "qq.com" || domain == "foxmail.com") {
                valid = valid || (domain == "qq.com" && /^[1-9][0-9]{4,10}$/.test(name));
                valid = valid || (/^[a-z][a-z0-9._-]{2,17}$/i.test(name) && !/([._-]){2,}/.test(name));
            } else if (domain == "sina.com" || domain == "sina.cn") {
                valid = valid || /^[a-z0-9][a-z0-9_]{2,14}[a-z0-9]$/.test(name);
            } else if (domain == "sohu.com") {
                valid = valid || /^[a-z][a-zA-Z0-9_]{3,15}$/.test(name);
            } else if (domain == "gmail.com") {
                valid = valid || (/^[a-z0-9][a-z0-9.]{4,28}[a-z0-9]$/i.test(name) && !/\.{2,}/.test(name) &&
                    (name.length < 8 || /[a-z]/.test(name)));
            } else if (domain == "outlook.com" || domain == "hotmail.com") {
                valid = valid || (/^[a-z][a-z0-9._-]{0,63}$/i.test(name) && !/\.{2,}/.test(name));
            } else if (domain == "yahoo.com" || domain == "yahoo.com.cn" || domain == "yahoo.cn") {
                valid = valid || (/^[a-z][a-z0-9._]{2,30}[a-z0-9]$/i.test(name) && !/_{2,}/.test(name) && name.match(/\./g).length < 2);
            } else {
                valid = valid || /^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/.test(value);
            }
            return valid;
        };
    },

    Future: function (pattern) {
        this.pattern = pattern || "yyyy-MM-dd HH:mm:ss";
        var sdf = new SimpleDateFormat(this.pattern);
        this.isValid = function (value) {
            if (!value) {
                return true;
            }
            try {
                var date = sdf.parse(value);
                return date.getTime() > new Date().getTime();
            } catch (e) {
                return false;
            }
        };
    },

    Past: function (pattern) {
        this.pattern = pattern || "yyyy-MM-dd HH:mm:ss";
        var sdf = new SimpleDateFormat(this.pattern);
        this.isValid = function (value) {
            if (!value) {
                return true;
            }
            try {
                var date = sdf.parse(value);
                return date.getTime() < new Date().getTime();
            } catch (e) {
                return false;
            }
        };
    },

    Length: function (min, max) {
        this.min = min;
        this.max = max;
        this.isValid = function (value) {
            if (!value) {
                return true;
            }
            var length = value.length;
            return length >= this.min && length <= this.max;
        };
    },

    Max: function (maxValue) {
        this.maxValue = maxValue;
        this.isValid = function (value) {
            if (!value) {
                return true;
            }
            value = Number(value);
            if (isNaN(value)) {
                return false;
            }
            return value <= this.maxValue;
        };
    },

    Min: function (minValue) {
        this.minValue = minValue;
        this.isValid = function (value) {
            if (!value) {
                return true;
            }
            value = Number(value);
            if (isNaN(value)) {
                return false;
            }
            return value >= this.minValue;
        };
    },
    
    Mobile: function () {
        this.isValid = function (value) {
            if (!value) {
                return true;
            }
            return /^(0|86|17951)?(13[0-9]|15[012356789]|17[3678]|18[0-9]|14[57])[0-9]{8}$/.test(value);
        };
    },

    NotBlank: function () {
        this.isValid = function (value) {
            if (!value) {
                return false;
            }
            return $.trim(value).length > 0;
        };
    },

    NotEmpty: function () {
        this.isValid = function (value) {
            if (value == null) {
                return false;
            }
            return value.length > 0;
        };
    },

    NotNull: function () {
        this.isValid = function (value) {
            return !!value;
        };
    },

    Null: function() {
        this.isValid = function (value) {
            return !value;
        };
    },

    Regex: function (regexp, flags) {
        this.regexp = regexp;
        this.flags = flags || 0;
        var attributes = "";
        if (this.flags & 0x02) {
            attributes += "i";
        }
        if (this.flags & 0x08) {
            attributes += "m";
        }
        // FIXME Not supported: DOTALL UNICODE_CASE CANON_EQ UNIX_LINES LITERAL UNICODE_CHARACTER_CLASS COMMENTS
        var regex = new RegExp(this.regexp, attributes);
        this.isValid = function (value) {
            if (!value) {
                return true;
            }
            return regex.test(value);
        };
    },

    Size: function (min, max) {
        this.min = min;
        this.max = max;
        this.isValid = function (value, validator, element, constraint) {
            var elements = validator.findByName(constraint.name);
            var length = elements.length;
            return length >= this.min && length <= this.max;
        };
    },

    URL: function (protocol, host, port) {
        this.protocol = protocol;
        this.host = host;
        this.port = port || -1;
        this.isValid = function (value) {
            if (!value || value.length == 0) {
                return true;
            }
            try {
                var url = $.parseURL(value);
            } catch (e) {
                return false;
            }
            if (this.protocol && this.protocol.length > 0 && url.protocol != this.protocol) {
                return false;
            }
            if (this.host && this.host.length > 0 && url.host != this.host) {
                return false;
            }
            if (this.port != -1 && url.port != this.port) {
                return false;
            }
            return true;
        };
    },

    EqualTo: function (name, ignoreCase) {
        this.name = name;
        this.ignoreCase = ignoreCase || false;
        this.isValid = function (value, validator) {
            var element = validator.findByName(this.name);
            var diffValue = validator.elementValue(element);
            return ignoreCase ? value.toLowerCase() == diffValue.toLowerCase()
                    : value == diffValue;
        };
    },
    
    Remote: function (url, params) {
        this.isValid = function (value, validator, element, constraint) {
            if (arguments.length < 3) 
                return false;

            var previous = validator.previousValue(element, url),
                depends = $(element).attr("depends"),
                data;
            
            if (previous.old === value) {
                return previous.valid;
            }
            previous.old = value;
            validator.startRequest(element);
            data = {};
            data["value"] = value;
            if (depends) {
                $.each(eval(depends), function (i, name) {
                    var elements = validator.findByName(name);
                    if (elements.length) {
                        data[name] = validator.elementValue(elements[0]);
                    }
                });
            }
            $.ajax({
                mode: "abort",
                port: "validate" + element.name,
                url: url,
                type: "post",
                dataType: "json",
                data: data,
                context: validator.currentForm,
                success: function(response) {
                    var valid = response === true || response === "true",
                        errors, message, submitted;
                    
                    previous.valid = valid;
                    if (valid) {
                        validator.element(element);
                    } else {
                        errors = {};
                        errors[element.name] = validator.getMessage(element, constraint);
                        validator.invalid[element.name] = true;
                        validator.showErrors(errors);
                    }
                    validator.stopRequest(element, valid);
                }
            });
            return "pending";
        };
        var self = this;
        params && $.each(params, function (n, i) {
            self[n] = i;
        });
    },

    Options: function (validators) {
        this.subValidators = validators;
        this.isValid = function (value, validator, element) {
            for (i in this.subValidators) {
                var va = this.subValidators[i];
                var valid = va && va.isValid(value, validator, element);
                if (valid == "pending" || valid === true) {
                    return valid;
                }
            }
            return false;
        };
    },

    Separator: function (validator, separator, ignoreLastBlank) {
        this.validator = validator;
        this.separator = separator;
        this.ignoreLastBlank = ignoreLastBlank || true;
        this.isValid = function (value, validator, element) {
            if (!value) {
                return true;
            }
            if (this.ignoreLastBlank) {
                var pos = value.lastIndexOf(this.separator);
                var lastString = value.substring(pos + 1);
                if (/\s+/.test(lastString)) {
                    value = value.substring(0, pos);
                }
            }
            var subTexts = value.split(this.separator);
            for (var subText in subTexts) {
                if (!this.validator.isValid(subText, validator, element)) {
                    return false;
                }
            }
            return true;
        };
    },

    MultipartSize: function (min, max) {
        this.min = min;
        this.max = max;
        this.isValid = function (file) {
            if (!file) {
                return true;
            }
            size = file.size;
            return size >= this.min && size <= this.max;
        };
    },

    ImageSize: function (minWidth, maxWidth, minHeight, maxHeight) {
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.isValid = function (image) {
            if (!image) {
                return true;
            }
            var width = image.width;
            var height = image.height;
            return width >= this.minWidth && width <= this.maxWidth &&
                height >= this.minHeight && height <= this.maxHeight;
        };
    },

    ImageRatio: function (ratio) {
        this.ratio = ratio;
        this.isValid = function (image) {
            if (!image) {
                return true;
            }
            var width = image.width;
            var height = image.height;
            return this.ratio == (width / height);
        };
    }
};