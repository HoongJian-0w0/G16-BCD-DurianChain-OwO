import axios, {
  type AxiosInstance,
  type AxiosResponse,
  type AxiosRequestConfig,
} from 'axios'
import message from '@/utils/message'
import router from '@/router'

export interface Result<T = any> {
  success: boolean,
  code: number
  message: string
  data: T
}

const config = {
  baseURL: 'http://localhost:9090',
  timeout: 5000,
}

class Http {
  private instance: AxiosInstance
  constructor(config: AxiosRequestConfig) {
    this.instance = axios.create(config)
    this.interceptors()
  }
  private interceptors() {
    this.instance.interceptors.request.use((config) => {
      const token = localStorage.getItem('token')
      if (token) {
        config.headers['Authorization'] = `Bearer ${token}`
      }
      return config
    }),
      (error: any) => {
        error.data = {}
        error.data.msg = 'Server error, please contact administrator'
        return error
      }
    this.instance.interceptors.response.use(
      (res: AxiosResponse) => {
        if (res.data.code === 200) {
          return res.data
        } else {
          if (res.data.code === 401) {
            router.push({ path: '/login' })
            message.error(res.data.message || 'Unauthorized, please log in again')
          } else {
            message.error(res.data.message || 'API error')
          }
          return Promise.reject(res.data)
        }
      },
      (error: any) => {
        console.log('Entered error handler')
        error.data = {}

        if (error && error.response) {
          switch (error.response.status) {
            case 400:
              error.data.msg = 'Bad Request'
              break
            case 401:
              error.data.msg = 'Unauthorized, please log in again'
              break
            case 403:
              error.data.msg = 'Forbidden'
              break
            case 404:
              error.data.msg = 'Not Found, API endpoint not available'
              break
            case 405:
              error.data.msg = 'Method Not Allowed'
              break
            case 408:
              error.data.msg = 'Request Timeout'
              break
            case 500:
              error.data.msg = 'Internal Server Error'
              break
            case 501:
              error.data.msg = 'Not Implemented'
              break
            case 502:
              error.data.msg = 'Bad Gateway'
              break
            case 503:
              error.data.msg = 'Service Unavailable'
              break
            case 504:
              error.data.msg = 'Gateway Timeout'
              break
            case 505:
              error.data.msg = 'HTTP Version Not Supported'
              break
            default:
              error.data.msg = `Unexpected status code: ${error.response.status}`
          }
          message.error(error.data.msg)
        } else {
          console.log(error)
          error.data.msg = 'Failed to connect to server'
          message.error(error.data.msg)
        }

        return Promise.reject(error)
      }
    )
  }

  // REST API
  post<T=Result>(url: string, data?: object): Promise<T> {
    return this.instance.post(url, data);
  }
  get<T=Result>(url: string, params?: object): Promise<T> {
    return this.instance.get(url, params);
  }
  put<T=Result>(url: string, data?: object): Promise<T> {
    return this.instance.put(url, data);
  }
  delete<T=Result>(url: string): Promise<T> {
    return this.instance.delete(url);
  }
  upload<T=Result>(url: string, params?: object): Promise<T> {
    return this.instance.post(url, params, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  }
}

export default new Http(config);