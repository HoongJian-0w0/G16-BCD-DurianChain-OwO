import sha256 from 'crypto-js/sha256';
import { ElMessage } from 'element-plus';
import message from "@/utils/message";

/**
 * Uploads image file to Cloudinary and returns the secure URL + SHA256 hash.
 * @param file - The image file to upload
 * @returns Object containing { imageUrl, imageHash }
 */
export async function uploadImageToCloudinary(file: File): Promise<{ imageUrl: string; imageHash: string }> {
    try {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('upload_preset', import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET);
        formData.append('folder', 'durian-images');

        const cloudName = import.meta.env.VITE_CLOUDINARY_CLOUD_NAME;
        const uploadUrl = `https://api.cloudinary.com/v1_1/${cloudName}/image/upload`;

        const res = await fetch(uploadUrl, {
            method: 'POST',
            body: formData
        });

        const data = await res.json();

        if (!data.secure_url) {
            throw new Error(data.error?.message || 'Cloudinary upload failed');
        }

        const hash = await getSHA256(file);

        return {
            imageUrl: data.secure_url,
            imageHash: hash
        };
    } catch (err: any) {
        console.error('[Cloudinary Upload Error]', err);
        message.error(`Upload failed: ${err.message}`);
        throw err;
    }
}

/**
 * Returns SHA256 hash of a file (File → ArrayBuffer → hash).
 * @param file - File object
 * @returns SHA256 hash as hex string
 */
export async function getSHA256(file: File): Promise<string> {
    const buffer = await file.arrayBuffer();
    const hashBuffer = await crypto.subtle.digest('SHA-256', buffer);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
}

/**
 * Verifies an image URL against a known SHA256 hash.
 * @param imageUrl - The full URL to the image
 * @param expectedHash - The expected SHA256 hash
 * @returns true if the hash matches, false otherwise
 */
export async function verifyImageHash(imageUrl: string, expectedHash: string): Promise<boolean> {
    try {
        const response = await fetch(imageUrl);
        const buffer = await response.arrayBuffer();
        const hashBuffer = await crypto.subtle.digest('SHA-256', buffer);
        const hashArray = Array.from(new Uint8Array(hashBuffer));
        const actualHash = hashArray.map(b => b.toString(16).padStart(2, '0')).join('');

        return actualHash === expectedHash;
    } catch (err: any) {
        console.error('[Verify Image Hash Error]', err);
        message.error('Failed to verify image hash');
        return false;
    }
}
