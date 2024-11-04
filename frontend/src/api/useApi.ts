import type { AxiosInstance, AxiosResponse, Method } from 'axios';
import { inject } from 'vue';
import { ElMessage } from 'element-plus';

export interface Api {
  get: <T>(url: string, params: RequestParams) => Promise<T>;
  post: <T>(url: string, payload: any, params: RequestParams) => Promise<T>;
  patch: <T>(url: string, payload: any, params: RequestParams) => Promise<T>;
  delete: <T>(url: string, params: RequestParams) => Promise<T>;
}

export interface RequestParams {
  [key: string]: any;
}

export function useApi(): Api {
  const axios: AxiosInstance = inject('axios') as AxiosInstance;

  async function request<T>(method: Method,
                            url: string,
                            payload?: any,
                            params?: RequestParams): Promise<T> {
    return axios.request({
      method: method,
      url: url,
      params: params,
      data: payload
    }).then((response: AxiosResponse<T>) => {
      return response.data;
    }).catch(error => {
      ElMessage.error({
        message: error.message
      });
      return Promise.reject(error);
    });
  }

  async function get<T>(url: string, params: RequestParams): Promise<T> {
    return request('GET', url, undefined, params);
  }

  async function post<T>(url: string, payload: any, params: RequestParams): Promise<T> {
    return request('POST', url, payload, params);
  }

  async function patch<T>(url: string, payload: any, params: RequestParams): Promise<T> {
    return request('PATCH', url, payload, params);
  }

  async function delete_<T>(url: string, params: RequestParams): Promise<T> {
    return request('DELETE', url, undefined, params);
  }

  return {
    get,
    post,
    patch,
    'delete': delete_
  };
}