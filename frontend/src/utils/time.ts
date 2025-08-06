/**
 * Converts a date input to a Solidity-compatible UNIX timestamp (in seconds).
 * Accepts string (e.g., '2025-08-07 22:30'), number (timestamp), or Date object.
 *
 * @param input - string | number | Date
 * @returns number - UNIX timestamp in seconds
 * @throws Error if input is invalid
 */
export function toSolidityTimestamp(input: string | number | Date): number {
    let date: Date;

    if (input instanceof Date) {
        date = input;
    } else if (typeof input === 'number') {
        date = new Date(input > 1e12 ? input : input * 1000); // detect ms vs s
    } else if (typeof input === 'string') {
        // string could be "2025-08-07 22:30" or "1754416500"
        const trimmed = input.trim();
        if (/^\d{10,}$/.test(trimmed)) {
            const timestamp = Number(trimmed);
            date = new Date(trimmed.length > 10 ? timestamp : timestamp * 1000);
        } else {
            date = new Date(trimmed);
        }
    } else {
        throw new Error('❌ Unsupported date input type');
    }

    if (isNaN(date.getTime())) {
        throw new Error('❌ Invalid date input');
    }

    return Math.floor(date.getTime() / 1000);
}

/**
 * Converts a UNIX timestamp in seconds (from Solidity) to readable local time string.
 *
 * @param unixSeconds - UNIX timestamp (in seconds)
 * @returns string - Local time in 'YYYY-MM-DD HH:mm:ss' format
 */
export function fromSolidityTimestamp(unixSeconds: number): string {
    const date = new Date(unixSeconds * 1000);

    if (isNaN(date.getTime())) return 'Invalid Date';

    const pad = (n: number) => n.toString().padStart(2, '0');

    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ` +
        `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
}
