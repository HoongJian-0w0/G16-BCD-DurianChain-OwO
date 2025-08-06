declare module '@vitejs/plugin-vue' {
    import { Plugin } from 'vite';
    const vuePlugin: () => Plugin;
    export default vuePlugin;
}
