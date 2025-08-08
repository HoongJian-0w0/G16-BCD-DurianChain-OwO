// /api/logistics/parcel/index.ts
import request from '@/utils/request';
import type { ParcelDTO } from './ParcelModels';

export function collectParcel(data: ParcelDTO) {
    return request.post('/parcel/collect', data);
}

export function confirmDBDelivery(data: Pick<ParcelDTO, 'batchId' | 'txHash'>) {
    return request.post('/parcel/deliver', data);
}