import { Html5Qrcode } from 'html5-qrcode';

let qrReader: Html5Qrcode | null = null;

/**
 * Start QR code scanning using the device camera.
 *
 * @param elementId - ID of the DOM element where the scanner will render (e.g. "qr-reader")
 * @param onSuccess - Callback with the decoded QR text when scan succeeds
 * @param onError - Optional callback when scanning errors occur
 */
export async function startQrScanner(
    elementId: string,
    onSuccess: (decodedText: string) => void,
    onError?: (error: any) => void
): Promise<void> {
    try {
        const cameras = await Html5Qrcode.getCameras();
        if (!cameras || cameras.length === 0) {
            throw new Error('No cameras found');
        }

        qrReader = new Html5Qrcode(elementId);

        await qrReader.start(
            cameras[0].id,
            {
                fps: 10,
                qrbox: 250,
            },
            (decodedText) => {
                onSuccess(decodedText);
            },
            (errorMessage) => {
                if (onError) {
                    onError(errorMessage);
                }
            }
        );
    } catch (error) {
        if (onError) onError(error);
        throw error;
    }
}

/**
 * Stop the camera QR scanner if running.
 */
export async function stopQrScanner(): Promise<void> {
    if (qrReader) {
        try {
            await qrReader.stop();
        } catch (err) {
            console.warn('[QR Scanner Stop Error]', err);
        } finally {
            qrReader = null;
        }
    }
}

/**
 * Scan a QR code from an image file (PNG, JPG, etc.)
 *
 * @param file - The image file selected by the user
 * @returns Promise<string> - The decoded QR text
 */
export async function scanQrFromFile(file: File): Promise<string> {
    // Use a temporary hidden container to avoid ID conflicts
    const containerId = 'qr-reader-temp';
    let container = document.getElementById(containerId);

    if (!container) {
        container = document.createElement('div');
        container.id = containerId;
        container.style.display = 'none';
        document.body.appendChild(container);
    }

    const fileScanner = new Html5Qrcode(containerId);
    try {
        const result = await fileScanner.scanFile(file, true);
        return result;
    } finally {
        await fileScanner.clear(); // clean up scanner
    }
}
