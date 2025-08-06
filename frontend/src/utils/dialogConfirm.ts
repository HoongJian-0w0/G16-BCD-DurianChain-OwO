import { ElMessageBox } from 'element-plus'

export default function dialogConfirm(text: string) {
  return new Promise((resolve, reject) => {
    ElMessageBox.confirm(text, 'System Confirmation', {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning',
      confirmButtonClass: 'custom-confirm-btn',
    })
      .then(() => {
        resolve(true)
      })
      .catch(() => {
        reject(true)
      })
  }).catch(() => {
    return false
  })
}
