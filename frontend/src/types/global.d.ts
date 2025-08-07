declare global {
    interface EthereumProvider {
        isMetaMask?: boolean;
        request: (args: { method: string; params?: unknown[] }) => Promise<any>;
        on?: (event: string, callback: (...args: any[]) => void) => void;
        removeListener?: (event: string, callback: (...args: any[]) => void) => void;
    }

    interface Window {
        ethereum?: EthereumProvider;
        _walletListenerAdded?: boolean
    }
}

export {};
