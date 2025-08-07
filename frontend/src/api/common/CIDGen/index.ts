import request from '@/utils/request'

/**
 * Upload a file to IPFS via backend
 * @param file File to upload (e.g., certificate, image, etc.)
 * @returns Promise resolving to IPFS CID and gateway URL
 */
export function uploadToIPFS(file: File) {
    const formData = new FormData();
    formData.append('file', file);

    return request.post('/ipfs/upload', formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });
}
