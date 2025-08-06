<!-- src/components/SvgIcon.vue -->
<template>
  <svg aria-hidden="true" :class="svgClass">
    <use :xlink:href="iconName" />
  </svg>
</template>

<script setup lang="ts">
import {computed} from "vue";

const props = defineProps<{
  name: string;
  className?: string;
}>();

const iconName = computed(() => `#icon-${props.name}`);
const svgClass = computed(() => props.className || 'svg-icon');
</script>

<style scoped>
.svg-icon {
  width: 1em;
  height: 1em;
  fill: currentColor;
}
</style>
