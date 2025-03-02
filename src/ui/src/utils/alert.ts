import {Modal} from "antd";

const { error, success } = Modal

export function showSuccess(msg: string) {
    success({
        content: msg,
        okText: '확인',
    })
}

export function showError(errorMsg: string) {
    error({
        content: errorMsg,
        okText: '확인',
        title: '오류',
    })
}
