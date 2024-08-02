/* import { i18n } from 'vue-i18n'
const { t } = useI18n() */

import i18n from '@/languages'
const { t } = i18n.global;

//校验字段方法
export function validateFields(component, fieldsToValidate, form) {
    return new Promise((resolve, reject) => {
        Promise.all(
            fieldsToValidate.map((field) => {
                return new Promise((resolve, reject) => {
                    component.$refs[form].validateField(field, (isValid) => {
                        resolve(isValid);
                    });
                });
            })
        ).then((validationResults) => {
            let valid = validationResults.every((isValid) => isValid);
            resolve(valid);
        }).catch((error) => {
            reject(error);
        });
    });
}

export const commonValidateNotEmptyAndMaxLength32 = () => {
  return [
    {required: true, message: t('common.rules.NotEmpty'), trigger: "blur"},
    {min: 1, max: 32, message: t('common.rules.NotEmptyAndMaxLength32'), trigger: 'blur'},
    { validator: (rule, value, callback) => {
        if(value == '' || value === undefined || value.trim()  === '') {
          callback(new Error(t('common.rules.NotEmpty')));
        } else {
          callback();
        }
      }, trigger: "blur"}
  ]
}

export const commonValidateNotEmptyAndMaxLength64 = () => {
  return [
    {required: true, message: t('common.rules.NotEmpty'), trigger: "blur"},
    {min: 1, max: 64, message: t('common.rules.NotEmptyAndMaxLength64'), trigger: 'blur'},
    { validator: (rule, value, callback) => {
        if(value == '' || value === undefined || value.trim()  === '') {
          callback(new Error(t('common.rules.NotEmpty')));
        } else {
          callback();
        }
      }, trigger: "blur"}
  ]
}


export const commonValidateNotEmptyAndMaxLength128 = () => {
  return [
    {required: true, message: t('common.rules.NotEmpty'), trigger: "blur"},
    {min: 1, max: 128, message: t('common.rules.NotEmptyAndMaxLength128'), trigger: 'blur'},
    { validator: (rule, value, callback) => {
        if(value == '' || value === undefined || value.trim()  === '') {
          callback(new Error(t('common.rules.NotEmpty')));
        } else {
          callback();
        }
      }, trigger: "blur"}
  ]
}

export const commonValidateNotEmptyAndMaxLength200 = () => {
  return [
    {required: true, message: t('common.rules.NotEmpty'), trigger: "blur"},
    {min: 1, max: 200, message: t('common.rules.NotEmptyAndMaxLength200'), trigger: 'blur'},
    { validator: (rule, value, callback) => {
        if(value == '' || value === undefined || value.trim()  === '') {
          callback(new Error(t('common.rules.NotEmpty')));
        } else {
          callback();
        }
      }, trigger: "blur"}
  ]
}

export const commonValidateNotEmptyAndMaxLength255 = () => {
  return [
    {required: true, message: t('common.rules.NotEmpty'), trigger: "blur"},
    {min: 1, max: 255, message: t('common.rules.NotEmptyAndMaxLength255'), trigger: 'blur'},
    { validator: (rule, value, callback) => {
        if(value == '' || value === undefined || value.trim()  === '') {
          callback(new Error(t('common.rules.NotEmpty')));
        } else {
          callback();
        }
      }, trigger: "blur"}
  ]
}

export const commonValidateNotEmptyAndMaxLength8192 = () => {
  return [
    {required: true, message: t('common.rules.NotEmpty'), trigger: "blur"},
    {min: 1, max: 8192, message: t('common.rules.NotEmptyAndMaxLength8192'), trigger: 'blur'},
    { validator: (rule, value, callback) => {
        if(value == '' || value === undefined || value.trim()  === '') {
          callback(new Error(t('common.rules.NotEmpty')));
        } else {
          callback();
        }
      }, trigger: "blur"}
  ]
}

export const commonValidateNotEmptyAndMaxLength4096 = () => {
  return [
    {required: true, message: t('common.rules.NotEmpty'), trigger: "blur"},
    {min: 1, max: 4096, message: t('common.rules.NotEmptyAndMaxLength4096'), trigger: 'blur'},
    { validator: (rule, value, callback) => {
        if(value == '' || value === undefined || value.trim()  === '') {
          callback(new Error(t('common.rules.NotEmpty')));
        } else {
          callback();
        }
      }, trigger: "blur"}
  ]
}

export const commonValidateEmptyAndMaxLength255AndUri = () => {
  return [
    {min: 0, max: 255, message: t('common.rules.NotEmptyAndMaxLength255'), trigger: 'blur'},
    { validator: (rule, value, callback) => {
        const urlregex = /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/;
        if(value == '' || value === undefined || value.trim()  === '') {
          callback();
        } else if(!urlregex.test(value)) {
          callback(new Error(t('common.rules.uri')));
        } else {
          callback();
        }
      }, trigger: "blur"}
  ]
}

export const commonValidateNotEmptyAndMaxLength255AndUri = () => {
  return [
    {required: true, message: t('common.rules.NotEmpty'), trigger: "blur"},
    {min: 1, max: 255, message: t('common.rules.NotEmptyAndMaxLength255'), trigger: 'blur'},
    { validator: (rule, value, callback) => {
        const urlregex = /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/;
        if(value == '' || value === undefined || value.trim()  === '') {
          callback(new Error(t('common.rules.NotEmpty')));
        } else if(!urlregex.test(value)) {
          callback(new Error(t('common.rules.uri')));
        }else {
          callback();
        }
      }, trigger: "blur"}
  ]
}


export const commonValidateNotEmptyAndMaxLength255AndLDAP = () => {
  return [
    {required: true, message: t('common.rules.NotEmpty'), trigger: "blur"},
    {min: 1, max: 255, message: t('common.rules.NotEmptyAndMaxLength255'), trigger: 'blur'},
    { validator: (rule, value, callback) => {
        const ldapRegex = /^(ldap?|ldaps):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/;
        if(value == '' || value === undefined || value.trim()  === '') {
          callback(new Error(t('common.rules.NotEmpty')));
        } else if(!ldapRegex.test(value)) {
          callback(new Error(t('common.rules.ldap')));
        }else {
          callback();
        }
      }, trigger: "blur"}
  ]
}


export const commonValidateNotEmptyAndMaxLength64AndNumber = () => {
  return [
    {required: true, message: t('common.rules.NotEmpty'), trigger: "blur"},
    {min: 1, max: 64, message: t('common.rules.NotEmptyAndMaxLength64'), trigger: 'blur'},
    { validator: (rule, value, callback) => {
        const regex = /^[0-9]+$/; // 定义只包含字母和数字的正则表达式
        if(value == '' || value === undefined || value.trim()  === '') {
          callback(new Error(t('common.rules.NotEmpty')));
        } else if (!regex.test(value)) {
          callback(new Error(t('common.rules.number')));
        } else {
          callback();
        }
      }, trigger: "blur"}
  ]
}

export const commonValidateNotEmptyAndMaxLength64AndNumberAndChar = () => {
  return [
    {required: true, message: t('common.rules.NotEmpty'), trigger: "blur"},
    {min: 1, max: 64, message: t('common.rules.NotEmptyAndMaxLength64'), trigger: 'blur'},
    { validator: (rule, value, callback) => {
        const regex = /^[a-zA-Z0-9]+$/;
        if(value == '' || value === undefined || value.trim()  === '') {
          callback(new Error(t('common.rules.NotEmpty')));
        } else if (!regex.test(value)) {
          callback(new Error(t('common.rules.numberChar')));
        } else {
          callback();
        }
      }, trigger: "blur"}
  ]
}

export const validatePass = (rule, value, callback, policy) => {
    console.log(policy)

    if (value === '') {
        callback(new Error(t('user.rule.pwd')));
        return;
    }

    let data = policy;
    let errors = [];

    if (data.minLength !== 0 && data.maxLength !== 0) {
        let inputLength = value.length;
        if (inputLength < data.minLength || inputLength > data.maxLength) {
            errors.push(`${t('user.rule.pwd1')}${data.minLength}-${data.maxLength}${t('user.rule.pwd2')}`);
        }
    }
    if (data.isLowerCase === 1) {
        // 必须包含小写字母
        if (!/[a-z]/.test(value)) {
            errors.push(t('user.rule.pwd3'));
        }
    }

    if (data.isDigit === 1) {
        // 必须包含阿拉伯数字
        if (!/\d/.test(value)) {
            errors.push(t('user.rule.pwd4'));
        }
    }

    if (data.isSpecial === 1) {
        // 必须包含标点符号
        if (!/[!@#$%^&*()_+={}\[\]:;<>,.?~\\-]/.test(value)) {
            errors.push(t('user.rule.pwd5'));
        }
    }

    if (/[\u4e00-\u9fa5]/.test(value)) {
        // 包含中文字符
        errors.push(t('user.rule.pwd6'));
    }

    if (/\s/.test(value)) {
        errors.push(t('user.rule.pwd7'));
    }

    if (errors.length > 0) {
        callback(new Error(errors.join(', ')));
    } else {
        callback();
    }
}
