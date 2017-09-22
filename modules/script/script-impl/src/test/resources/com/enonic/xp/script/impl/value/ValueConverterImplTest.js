(function () {
    var assert = Java.type('org.junit.Assert');

    return {
        fromJs_int: function () {
            return 1;
        },
        fromJs_double: function () {
            return 1.5;
        },
        fromJs_string: function () {
            return 'test';
        },
        fromJs_boolean: function () {
            return true;
        },
        fromJs_null: function () {
            return null;
        },
        fromJs_undefined: function () {
            return undefined;
        },
        fromJs_array: function () {
            return [1, 2, 3];
        },
        fromJs_array_complex: function () {
            return [1, [2, 3], 4, {}];
        },
        fromJs_object: function () {
            return {
                a: 1,
                b: 2
            };
        },
        fromJs_object_complex: function () {
            return {
                a: 1,
                b: {},
                c: []
            };
        },
        fromJs_date: function () {
            return new Date(1234);
        },
        fromJs_function: function () {
            return function () {
                return 1;
            };
        },
        fromJs_function_args: function () {
            return function (a, b) {
                return {
                    num: a.num,
                    date: b instanceof Date
                };
            };
        },
        toJs_date: function (value) {
            assert.assertEquals(true, value instanceof Date);
            assert.assertEquals(1234, value.getTime(), 0);
        },
        toJs_array: function (value) {
            assert.assertEquals(true, value instanceof Array);
            assert.assertEquals('[1,2,3,[]]', JSON.stringify(value));
        },
        toJs_object: function (value) {
            assert.assertEquals(true, value instanceof Object);
            assert.assertEquals('{"a":1,"b":2,"c":{}}', JSON.stringify(value));
        },
        toJs_function: function (value) {
            var result = value(new Date(1234));
            assert.assertEquals(true, result instanceof Date);
            assert.assertEquals(1234, result.getTime(), 0);
        }
    };
});
