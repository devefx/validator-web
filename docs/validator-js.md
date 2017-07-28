# Validator Js Documents






### 扩展 - 自定义验证

示例

```javascript

$.extend($.validator.constraints, {
  Range: function(min, max) {
    this.min = min;
    this.max = max;
    this.isValid = function(value, validator, element, constraint) {
      if (!value) {
        return true;
      }
      value = Number(value);
      if (isNaN(value)) {
        return false;
      }
      return value >= min && value <= max;
    }
  }
});
```


 
