import { ElMessage } from 'element-plus'

type MessageType = 'success' | 'warning' | 'error'
type MessageCallback = () => void

/**
 * Show a global message with optional callback
 */
const showMessage = (
  message: string,
  callback?: MessageCallback,
  type: MessageType = 'success',
): void => {
  ElMessage({
    type,
    message,
    duration: 2000,
    onClose: () => {
      if (callback) {
        callback()
      }
    },
  })
}

/**
 * Exported message utility with typed methods
 */
const message = {
  error: (msg: string, callback?: MessageCallback) => {
    showMessage(msg, callback, 'error')
  },
  success: (msg: string, callback?: MessageCallback) => {
    showMessage(msg, callback, 'success')
  },
  warning: (msg: string, callback?: MessageCallback) => {
    showMessage(msg, callback, 'warning')
  },
}

export default message
