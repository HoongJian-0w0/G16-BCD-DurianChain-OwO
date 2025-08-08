import request from '@/utils/request';
import type { OrderDTO } from './OrderModels';

export function placeOrder(data: OrderDTO) {
    return request.post('/order/place', data);
}

export function cancelDBOrder(data: Pick<OrderDTO, 'batchId' | 'txHash'>) {
    return request.post('/order/cancel', data);
}