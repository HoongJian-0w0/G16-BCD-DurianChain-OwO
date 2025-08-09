<template>
  <div class="dc-landing">
    <!-- Hero -->
    <section class="hero">
      <div class="hero-copy">
        <h1 class="title">Trace Every Durian, Trust Every Bite</h1>
        <p class="subtitle">
          <span class="typing">
            Scan a QR to verify batch origin, certificates, and milestones.
          </span>
        </p>
      </div>
      <div class="hero-blob"></div>
    </section>

    <!-- Actions Row -->
    <div class="grid">
      <el-card class="scan-card" shadow="hover">
        <div class="scan-inner">
          <div class="scan-art">
            <img src="@/assets/image/Durian-Banner-Login.jpg" alt="Scan QR" />
          </div>

          <div class="scan-cta">
            <h3 class="scan-title">Scan or Upload QR Code</h3>
            <p class="scan-desc">Scan with camera, upload a QR image, or paste an ID.</p>

            <div class="scan-controls">
              <el-button type="primary" size="large" round @click="openQrDialog">
                <el-icon class="mr-6"><VideoCameraFilled /></el-icon>
                Scan with Camera
              </el-button>

              <el-upload
                  action="#"
                  :auto-upload="false"
                  :show-file-list="false"
                  accept="image/*"
                  :on-change="onUploadQR"
              >
                <el-button type="success" size="large" round>
                  <el-icon class="mr-6"><UploadFilled /></el-icon>
                  Upload QR Image
                </el-button>
              </el-upload>

              <el-input
                  v-model="manualId"
                  placeholder="Paste Batch ID / Durian ID / QR content"
                  clearable
                  class="id-input"
                  @keyup.enter="handleResolveId"
              >
                <template #suffix>
                  <el-button type="primary" size="small" @click="handleResolveId">Verify</el-button>
                </template>
              </el-input>
            </div>

            <div class="post-verify" v-if="resolved.batchId">
              <el-alert :title="`Verified: ${resolved.batchId}`" type="success" show-icon />
              <div class="meta-chips">
                <el-tag v-if="resolved.variety" round type="success">Variety: {{ resolved.variety }}</el-tag>
                <el-tag v-if="resolved.status" round type="info">Status: {{ resolved.status }}</el-tag>
                <el-tag v-if="resolved.farmId" round type="warning">Farm: {{ resolved.farmId }}</el-tag>

                <el-tag v-if="resolved.traderAgencyId" round type="primary">
                  Agency ID: {{ resolved.traderAgencyId }}
                </el-tag>
                <el-tag v-else-if="viewData.traderAddress" round type="primary" effect="plain">
                  Agency Addr: {{ short(viewData.traderAddress) }}
                </el-tag>

                <el-tag v-if="resolved.logisticsCompanyId" round type="danger">
                  Logistics ID: {{ resolved.logisticsCompanyId }}
                </el-tag>
                <el-tag v-else-if="viewData.logisticsAddress" round type="danger" effect="plain">
                  Logistics Addr: {{ short(viewData.logisticsAddress) }}
                </el-tag>
              </div>

              <div class="detail-buttons">
                <el-button :disabled="!canViewFarm" @click="openFarm" round>
                  <el-icon class="mr-6"><HomeFilled /></el-icon>
                  View Farm Details
                </el-button>

                <el-button :disabled="!canViewAgency" @click="openAgency" round>
                  <el-icon class="mr-6"><UserFilled /></el-icon>
                  View Agency Details
                </el-button>

                <el-button :disabled="!canViewLogistics" @click="openLogistics" round>
                  <el-icon class="mr-6"><Van /></el-icon>
                  View Logistics Details
                </el-button>

                <el-button :disabled="!canViewBatch" type="primary" @click="viewVisible = true" round>
                  View Batch Details
                </el-button>

                <el-button
                    v-if="highlightDurianId"
                    type="primary"
                    plain
                    round
                    @click="openDurianModal"
                >
                  View Durian
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="tips-card" shadow="never">
        <div class="tips-inner">
          <h3>How it works</h3>
          <ol>
            <li>Scan / upload QR, or paste ID</li>
            <li>If it's a Durian ID, we validate via backend and get its Batch ID</li>
            <li>Then we load the Batch on-chain and show details</li>
          </ol>
        </div>
      </el-card>
    </div>

    <!-- Milestones -->
    <section class="milestones">
      <div class="table-head">
        <h2>Milestones</h2>
        <el-tag v-if="!resolved.batchId" type="info" effect="plain">No data yet — scan or upload to load milestones.</el-tag>
      </div>

      <el-table
          v-loading="loading"
          :data="milestones"
          border
          empty-text="No milestones — scan or upload to view."
      >
        <el-table-column prop="time" label="Timestamp" width="200" />
        <el-table-column prop="action" label="Action" width="180" />
        <el-table-column prop="actorRole" label="Actor Role" width="140" />
        <el-table-column prop="actor" label="Actor Address / ID" min-width="240" />
        <el-table-column prop="notes" label="Notes" min-width="260" show-overflow-tooltip />
      </el-table>
    </section>

    <!-- Farm Dialog -->
    <el-dialog v-model="dialogs.farm" title="Farm Details" width="580px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="Farm ID">{{ farm?.farmId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Owner">{{ farm?.owner || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Location">{{ farm?.location || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Certificate CID">{{ farm?.certificateCID || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Certificate Expiry">
          {{ farm?.certificateExpiry ? formatTs(farm!.certificateExpiry) : '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- Agency Dialog -->
    <el-dialog v-model="dialogs.agency" title="Trader Agency Details" width="580px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="Agency ID">{{ agency?.agencyId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Owner">{{ agency?.owner || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Agency Name">{{ agency?.agencyName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Export License CID">{{ agency?.exportLicenseCID || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Export License Expiry">
          {{ agency?.exportLicenseExpiry ? formatTs(agency!.exportLicenseExpiry) : '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- Logistics Dialog -->
    <el-dialog v-model="dialogs.logistics" title="Logistics Company Details" width="580px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="Company ID">{{ logistics?.companyId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Owner">{{ logistics?.owner || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Company Name">{{ logistics?.companyName || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- Camera Scan Dialog -->
    <el-dialog
        v-model="qrDialogVisible"
        title="Scan QR with Camera"
        width="560px"
        destroy-on-close
        @close="stopScanner"
    >
      <div style="display:flex;flex-direction:column;gap:12px">
        <div id="qr-reader" style="width:100%;max-width:520px;height:320px;border:1px solid #ddd;border-radius:10px;overflow:hidden" />
        <el-alert v-if="scanError" :title="scanError" type="error" show-icon />
        <div style="display:flex;gap:8px;justify-content:flex-end">
          <el-button @click="stopScanner">Stop</el-button>
          <el-button type="primary" @click="restartScanner">Restart</el-button>
        </div>
      </div>
    </el-dialog>


    <!-- Durian Scan Modal -->
    <el-dialog v-model="dialogs.durian" title="Durian Scan Result" width="950px">
      <div style="display:flex; gap:16px; align-items:flex-start;">
        <el-image
            v-if="durianBackend?.imageUrl"
            :src="durianBackend.imageUrl"
            fit="cover"
            style="width: 240px; height: 240px; border-radius: 10px; flex: 0 0 auto;"
            :preview-src-list="[durianBackend.imageUrl]"
            preview-teleported
        />
        <el-descriptions :column="1" border style="flex:1">
          <el-descriptions-item label="Durian ID">{{ durianScan.durianId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="Batch ID">{{ durianBackend?.batchId || resolved.batchId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="Backend Image Hash">{{ durianScan.backendHash || '-' }}</el-descriptions-item>
          <el-descriptions-item label="On-chain Image Hash">{{ durianScan.onChainHash || '-' }}</el-descriptions-item>
          <el-descriptions-item label="Hash Match">
            <el-tag :type="durianScan.matchState === 'match' ? 'success' : durianScan.matchState === 'mismatch' ? 'danger' : 'info'">
              {{ durianScan.matchState === 'match' ? 'Matched' : durianScan.matchState === 'mismatch' ? 'Not Matched' : 'Unknown' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="Total Scan Count">{{ durianScan.scanCount ?? '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="dialogs.durian = false">Close</el-button>
      </template>
    </el-dialog>

    <!-- Batch Details Drawer -->
    <el-drawer v-model="viewVisible" title="Batch Details" size="850px">
      <el-descriptions :column="1" border style="max-width: 800px;">
        <el-descriptions-item label="Batch ID">{{ viewData.batchId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Food Name">{{ viewData.foodName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Farm ID">{{ viewData.farmId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Quantity">{{ viewData.quantity || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Farm Location">{{ viewData.farmLocation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Delivery Destination">{{ viewData.deliveryDestination || '-' }}</el-descriptions-item>

        <el-descriptions-item label="Farmer Address"><span class="addr">{{ viewData.farmerAddress || '-' }}</span></el-descriptions-item>
        <el-descriptions-item label="Trader Address"><span class="addr">{{ viewData.traderAddress || '-' }}</span></el-descriptions-item>
        <el-descriptions-item label="Logistics Address"><span class="addr">{{ viewData.logisticsAddress || '-' }}</span></el-descriptions-item>

        <el-descriptions-item label="Tx Hash"><span class="addr">{{ viewData.txHash || '-' }}</span></el-descriptions-item>
        <el-descriptions-item label="Status">
          <el-tag :type="statusTagType" effect="dark">
            {{ viewData.status || '-' }}
          </el-tag>
        </el-descriptions-item>

        <el-descriptions-item label="Image">
          <el-image v-if="viewData.batchImageUrl" :src="viewData.batchImageUrl" style="width: 350px;" fit="contain" />
          <span v-else>-</span>
        </el-descriptions-item>

        <el-descriptions-item v-if="showDuriansTable" label="Durians" :span="1">
          <div v-if="viewData.durians?.length">
            <el-table :data="viewData.durians" style="width: 100%; margin-top: 12px;" size="small" border>
              <el-table-column label="Durian ID" prop="durianId" />
              <el-table-column label="Image" width="120">
                <template #default="{ row }">
                  <el-image :src="row.imageUrl" fit="cover" style="width: 80px; height: 80px; border-radius: 6px;" :preview-src-list="[row.imageUrl]" preview-teleported />
                </template>
              </el-table-column>
              <el-table-column label="Image Hash" prop="imageHash" />
            </el-table>
          </div>
          <div v-else><el-empty description="No durians found for this batch" /></div>
        </el-descriptions-item>
      </el-descriptions>
    </el-drawer>


    <input ref="fileInputRef" type="file" accept="image/*" style="display:none" />
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, nextTick, computed } from 'vue'
import { UploadFilled, UserFilled, HomeFilled, Van, VideoCameraFilled } from '@element-plus/icons-vue'
import message from '@/utils/message'

import { scanQrFromFile, startQrScanner, stopQrScanner } from '@/utils/qrscan'
import { getBatchById, getBatchMilestone } from '@/contracts/farmer/batchContract'
import { getDuriansByBatchId } from '@/api/farmer/durian' // keep if you render the durians table
import { getFarmById } from '@/contracts/farmer/farmContract'
import { getTraderAgencyById } from '@/contracts/trader/agencyContract'
import { getLogisticsCompanyById } from '@/contracts/logistics/logisticsCompanyContract'

// NEW: backend scan + on-chain durian list (only these)
import { scanDurian } from '@/api/farmer/durian'
import { getDuriansOnchainByBatchId } from '@/contracts/farmer/batchContract'

// ---------- Helpers ----------
const ZERO_ADDR = '0x0000000000000000000000000000000000000000'
const IPFS_GATEWAY = (import.meta as any)?.env?.VITE_IPFS_GATEWAY || 'https://gateway.pinata.cloud/ipfs/'
const gw = (s: string) => s.replace(/\/+$/,'') + '/'
function buildIpfsUrl(cid?: string) {
  if (!cid) return ''
  const clean = cid.replace(/^ipfs:\/\//, '').replace(/^\/?ipfs\//, '')
  return gw(IPFS_GATEWAY) + clean
}
function isZeroAddress(a?: string) { return !a || a.toLowerCase() === ZERO_ADDR.toLowerCase() }
function cleanAddress(a?: string) { return isZeroAddress(a) ? '' : (a || '') }
function short(a?: string) { const x = cleanAddress(a); return x && x.startsWith('0x') ? `${x.slice(0,6)}…${x.slice(-4)}` : (x || '') }
const hasVal = (v: any) => v !== undefined && v !== null && String(v).trim().length > 0

const statusTypeMap: Record<string, 'success'|'info'|'warning'|'danger'> = {
  Created: 'info',
  Ordered: 'warning',
  Collected: 'warning',
  Delivered: 'success',
  Deleted: 'danger',
}

const statusTagType = computed(() => {
  const k = (viewData.value?.status ?? '').toString().trim()
  return statusTypeMap[k] ?? 'info'
})

function normalizeBatch(b: any) {
  if (Array.isArray(b)) {
    return {
      batchId: b[0],
      variety: b[1],
      farmId: b[2],
      quantity: typeof b[3] === 'bigint' ? Number(b[3]) : Number(b[3] ?? 0),
      farmLocation: b[4],
      status: Number(b[5]),
      farmer: cleanAddress(b[6]),
      trader: cleanAddress(b[7]),
      logistics: cleanAddress(b[8]),
      traderAgencyId: b[9],
      logisticsCompanyId: b[10],
      batchImageCID: b[11],
      deliveryDestination: b[12],
    }
  }
  return {
    batchId: b?.batchId,
    variety: b?.variety,
    farmId: b?.farmId,
    quantity: typeof b?.quantity === 'bigint' ? Number(b.quantity) : Number(b?.quantity ?? 0),
    farmLocation: b?.farmLocation,
    status: Number(b?.status ?? 0),
    farmer: cleanAddress(b?.farmer),
    trader: cleanAddress(b?.trader),
    logistics: cleanAddress(b?.logistics),
    traderAgencyId: b?.traderAgencyId,
    logisticsCompanyId: b?.logisticsCompanyId,
    batchImageCID: b?.batchImageCID,
    deliveryDestination: b?.deliveryDestination,
  }
}

const statusMap = ['Created','Ordered','Collected','Delivered','Deleted'] as const
const toStatusText = (s: number) => statusMap[s] ?? 'Created'

function formatTs(ts: number) { return new Date(ts * 1000).toLocaleString() }
function extractBatchIdFromContent(content: string): string {
  const str = (content || '').trim()
  if (!str) return ''
  try { const u = new URL(str); return (u.searchParams.get('batchId') || u.searchParams.get('id') || '').trim() || str } catch {}
  return str
}

function looksLikeBatchId(s: string) { return /^batch([-_]|$)/i.test(s) }
function looksLikeDurianId(s: string) { return /^durian([-_]|$)/i.test(s) }

// Robust un-wrapper for backend scan response
function normalizeScanResponse(scanRes: any): { success: boolean; durian: any | null } {
  const top = (scanRes && typeof scanRes === 'object' && 'data' in scanRes)
      ? (scanRes as any).data   // axios response body
      : scanRes                 // already-unwrapped body

  const success =
      typeof top?.success === 'boolean' ? top.success
          : typeof scanRes?.success === 'boolean' ? scanRes.success
              : false

  const durian =
      top?.data?.durian ?? top?.durian ?? scanRes?.data?.durian ?? null

  return { success, durian }
}

// ---------- Types ----------
type Milestone = { time: string; action: string; actorRole: 'Admin' | 'Farmer' | 'Trader' | 'Logistics' | 'Unknown'; actor: string; notes?: string }
type Resolved = {
  batchId?: string
  variety?: string
  status?: 'Created' | 'Ordered' | 'Collected' | 'Delivered' | 'Deleted'
  farmId?: string
  traderAgencyId?: string
  logisticsCompanyId?: string
}
type Farm = { farmId: string; owner: string; location: string; certificateCID?: string; certificateExpiry?: number }
type Agency = { agencyId: string; owner: string; agencyName: string; exportLicenseCID?: string; exportLicenseExpiry?: number }
type Logistics = { companyId: string; owner: string; companyName: string }

// ---------- State ----------
const manualId = ref('')
const resolved = reactive<Resolved>({})
const milestones = ref<Milestone[]>([])
const loading = ref(false)

const dialogs = reactive({ farm: false, agency: false, logistics: false, durian: false })
const farm = ref<Farm | null>(null)
const agency = ref<Agency | null>(null)
const logistics = ref<Logistics | null>(null)

const qrDialogVisible = ref(false)
const scanning = ref(false)
const scanError = ref('')

const viewVisible = ref(false)
const viewData = ref<any>({})

const highlightDurianId = ref<string | null>(null)
const durianBackend = ref<any | null>(null)
const durianScan = reactive<{
  durianId?: string
  onChainHash?: string
  backendHash?: string
  matchState?: 'match' | 'mismatch' | 'unknown'
  scanCount?: number
}>({})

function computeMatch(onChain?: string, backend?: string): 'match' | 'mismatch' | 'unknown' {
  if (!onChain && !backend) return 'unknown'
  if (!onChain || !backend) return 'unknown'
  return String(onChain).toLowerCase() === String(backend).toLowerCase() ? 'match' : 'mismatch'
}

// ---------- Core resolve flow (Durian-first, safe) ----------
async function resolveAndLoad(batchOrDurianId: string) {
  const raw = extractBatchIdFromContent(batchOrDurianId).trim()
  if (!raw) {
    message.warning('No ID detected.')
    return
  }

  manualId.value = raw
  loading.value = true
  highlightDurianId.value = null
  durianBackend.value = null

  try {
    let batchId: string | null = null

    if (raw.startsWith('BATCH')) {
      // Directly handle batch ID
      batchId = raw
    } else if (raw.startsWith('DURIAN')) {
      // Scan durian, get batchId from backend
      const scanRes = await scanDurian(raw)
      const { success, durian } = normalizeScanResponse(scanRes)
      if (success && durian?.batchId) {
        durianBackend.value = durian
        highlightDurianId.value = durian.durianId || raw
        batchId = durian.batchId
        manualId.value = durian.durianId || raw
      } else {
        message.error('Durian not found by backend scan. Cannot resolve batch.')
        return
      }
    } else {
      message.error('Invalid ID format.')
      return
    }

    // === Fetch batch from on-chain ===
    const rawBatch = await getBatchById(batchId!)
    if (!rawBatch) throw new Error('Batch not found')
    const batch = normalizeBatch(rawBatch)

    Object.assign(resolved, {
      batchId: batch.batchId,
      variety: batch.variety || '',
      status: toStatusText(Number(batch.status ?? 0)),
      farmId: batch.farmId || '',
      traderAgencyId: batch.traderAgencyId || '',
      logisticsCompanyId: batch.logisticsCompanyId || '',
    })

    // === Milestones ===
    const rawMs = await getBatchMilestone(batchId!)
    milestones.value = (rawMs || []).map((m: any) => ({
      time: new Date(parseInt(m.timestamp) * 1000).toLocaleString(),
      action: m.action || '—',
      actorRole: (m.role || 'Unknown') as Milestone['actorRole'],
      actor: m.actor || '—',
      notes: m.description || '',
    }))

    // === Durians ===
    const dRes = await getDuriansByBatchId(batchId!)
    const durianList = (dRes?.data?.durians || []).map((d: any) => ({
      durianId: d.durianId,
      imageUrl: d.imageUrl || buildIpfsUrl(d.imageCid || d.imageCID || d.image_cid),
      imageHash: d.imageHash || d.hash || '',
    }))

    const batchImageUrl = buildIpfsUrl(batch?.batchImageCID)

    viewData.value = {
      batchId: batch.batchId,
      foodName: batch.variety || '',
      farmId: batch.farmId || '',
      quantity: Number(batch.quantity ?? durianList.length),
      farmLocation: batch.farmLocation || '',
      deliveryDestination: batch.deliveryDestination || '',
      farmerAddress: batch.farmer || '',
      traderAddress: batch.trader || '',
      logisticsAddress: batch.logistics || '',
      txHash: '',
      status: toStatusText(Number(batch.status ?? 0)),
      batchImageUrl,
      durians: durianList,
    }

    message.success(`Loaded batch ${batch.batchId}`)
    viewVisible.value = true

    if (highlightDurianId.value) await openDurianModal()
  } catch (e: any) {
    console.error('[resolveAndLoad]', e)
    message.error(e?.reason || e?.message || 'Failed to load record')
  } finally {
    loading.value = false
  }
}


// ---------- Durian modal ----------
async function openDurianModal() {
  try {
    const durId = (durianBackend.value?.durianId || highlightDurianId.value || manualId.value) as string
    const batchId = resolved.batchId as string
    if (!hasVal(durId)) return message.warning('Durian ID not available')
    if (!hasVal(batchId)) return message.warning('Batch ID not available')

    // On-chain list → find same durian → get on-chain hash
    const onChainList = await getDuriansOnchainByBatchId(batchId)
    const onChainItem = onChainList.find(d => String(d.id) === String(durId))

    durianScan.durianId = durId
    durianScan.onChainHash = onChainItem?.imageHash || ''
    durianScan.backendHash = durianBackend.value?.imageHash || ''
    durianScan.scanCount = durianBackend.value?.scanCount
    durianScan.matchState = computeMatch(durianScan.onChainHash, durianScan.backendHash)

    dialogs.durian = true
  } catch (err:any) {
    console.error('[openDurianModal]', err)
    message.error(err?.message || 'Failed to open durian details')
  }
}

// ---------- QR image upload ----------
async function onUploadQR(file: any) {
  try {
    if (!file?.raw) return
    const decoded = await scanQrFromFile(file.raw)
    manualId.value = decoded
    await resolveAndLoad(decoded)
  } catch (err) {
    console.error('[onUploadQR]', err)
    message.error('Image scan failed')
  }
}

// ---------- Manual input ----------
async function handleResolveId() {
  if (!manualId.value?.trim()) return message.warning('Please paste a Batch/Durian ID or QR content.')
  await resolveAndLoad(manualId.value)
}

// ---------- Live camera scan ----------
function openQrDialog() { qrDialogVisible.value = true; scanError.value = ''; nextTick(() => startLiveScanner()) }
function startLiveScanner() {
  scanning.value = true
  startQrScanner(
      'qr-reader',
      async (decodedText: string) => {
        stopScanner()
        qrDialogVisible.value = false
        manualId.value = decodedText
        await resolveAndLoad(decodedText)
      },
      (err: any) => { if (!String(err || '').toLowerCase().includes('notfound')) scanError.value = 'Camera error: ' + (err?.message || err || 'Unknown') }
  )
}
function stopScanner() { if (!scanning.value) return; stopQrScanner?.(); scanning.value = false }
function restartScanner() { stopScanner(); nextTick(() => startLiveScanner()) }

// ---------- Open detail dialogs ----------
async function openFarm() {
  const id = resolved.farmId
  if (!hasVal(id)) {
    farm.value = { farmId: viewData.value?.farmerAddress || '(unknown)', owner: viewData.value?.farmerAddress || '-', location: viewData.value?.farmLocation || '-' }
    dialogs.farm = true
    return
  }
  try {
    const chain = await getFarmById(id!)
    const expirySec = Number(chain?.certificateExpiry || 0)
    farm.value = {
      farmId: id!,
      owner: chain?.owner || viewData.value?.farmerAddress || '-',
      location: chain?.location || viewData.value?.farmLocation || '-',
      certificateCID: chain?.certificateCID || '',
      certificateExpiry: expirySec > 0 ? expirySec : undefined,
    }
    dialogs.farm = true
  } catch (err:any) {
    console.error('[openFarm]', err)
    message.error(err?.message || 'Failed to load farm details')
  }
}

async function openAgency() {
  const id = resolved.traderAgencyId
  const addr = cleanAddress(viewData.value?.traderAddress)
  if (!hasVal(id) && !hasVal(addr)) return

  try {
    if (hasVal(id)) {
      const a = await getTraderAgencyById(id!)
      const expirySec = Number(a?.exportLicenseExpiry || 0)
      agency.value = {
        agencyId: a?.agencyId || id!,
        owner: a?.owner || addr || '-',
        agencyName: a?.agencyName || '-',
        exportLicenseCID: a?.exportLicenseCID || '',
        exportLicenseExpiry: expirySec > 0 ? expirySec : undefined,
      }
    } else {
      agency.value = { agencyId: addr!, owner: addr!, agencyName: '(address only — ID not set)' }
    }
    dialogs.agency = true
  } catch (err:any) {
    console.error('[openAgency]', err)
    message.error(err?.message || 'Failed to load agency details')
  }
}

async function openLogistics() {
  const id = resolved.logisticsCompanyId
  const addr = cleanAddress(viewData.value?.logisticsAddress)
  if (!hasVal(id) && !hasVal(addr)) return

  try {
    if (hasVal(id)) {
      const l = await getLogisticsCompanyById(id!)
      logistics.value = { companyId: l?.companyId || id!, owner: l?.owner || addr || '-', companyName: l?.companyName || '-' }
    } else {
      logistics.value = { companyId: addr!, owner: addr || '-', companyName: '(address only — ID not set)' }
    }
    dialogs.logistics = true
  } catch (err:any) {
    console.error('[openLogistics]', err)
    message.error(err?.message || 'Failed to load logistics details')
  }
}

// ---------- Enable checks + table visibility ----------
const canViewFarm = computed(() => hasVal(resolved.farmId) || hasVal(cleanAddress(viewData.value?.farmerAddress)))
const canViewAgency = computed(() => hasVal(resolved.traderAgencyId) || hasVal(cleanAddress(viewData.value?.traderAddress)))
const canViewLogistics = computed(() => hasVal(resolved.logisticsCompanyId) || hasVal(cleanAddress(viewData.value?.logisticsAddress)))
const canViewBatch = computed(() => hasVal(resolved.batchId))

// Hide Durians table in drawer when this interaction was a durian scan
const showDuriansTable = computed(() => !highlightDurianId.value)
</script>

<style scoped>
.dc-landing {
  padding: 24px;
  display: flex;
  flex-direction: column;
  background-image:
      linear-gradient(90deg, rgba(116, 195, 101, 0.02), rgba(163, 217, 119, 0.1)),
      url('@/assets/svg/star-bg.svg');
  gap: 24px;
  min-height: 100vh;
}

/* Hero */
.hero {
  position: relative;
  border-radius: 18px;
  padding: 32px 24px;
  color: #e5e7eb;
  background: linear-gradient(135deg, #0f172a, #111827);
  overflow: hidden;
  isolation: isolate;
}
.hero-blob {
  content: "";
  position: absolute;
  right: -80px; top: -80px;
  width: 360px; height: 360px; border-radius: 50%;
  filter: blur(60px);
  background:
      radial-gradient(circle at 30% 30%, rgba(116,195,101,.32), transparent 60%),
      radial-gradient(circle at 70% 70%, rgba(214,223,114,.32), transparent 60%);
  opacity: .8; z-index: 0;
}
.hero-copy { position: relative; z-index: 1; }
.title {
  margin: 0 0 6px;
  font-size: 28px;
  font-weight: 800;
  background: linear-gradient(90deg, #74c365, #d6df72);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.subtitle { margin: 0; opacity: .9; }

/* Grid layout */
.grid {
  display: grid;
  grid-template-columns: 1.7fr 1fr;
  gap: 16px;
}
@media (max-width: 980px) {
  .grid { grid-template-columns: 1fr; }
}

/* Scan card */
.scan-card { border: none; border-radius: 18px; }
.scan-inner {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 18px;
  align-items: center;
}
@media (max-width: 760px) { .scan-inner { grid-template-columns: 1fr; } }
.scan-art {
  width: 100%;
  height: 260px;
  border-radius: 14px;
  overflow: hidden;
  background: #f5f7fa;
}
.scan-art img { width: 100%; height: 100%; object-fit: cover; }
.scan-title {
  margin: 0;
  font-size: 18px;
  font-weight: 800;
  background: linear-gradient(90deg, #74c365, #d6df72);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
.scan-desc { margin: 4px 0 12px; color: #6b7280; }
.scan-controls { display: flex; gap: 10px; flex-wrap: wrap; }
.id-input { max-width: 360px; }

.post-verify { margin-top: 14px; display: flex; flex-direction: column; gap: 10px; }
.meta-chips { display: flex; flex-wrap: wrap; gap: 8px; }
.detail-buttons { display: flex; gap: 10px; flex-wrap: wrap; }

/* Tips card */
.tips-card { background: #fafafa; border-radius: 18px; }
.tips-inner { display: flex; flex-direction: column; gap: 8px; }
.tips-inner h3 { margin: 0; }
.mt-16 { margin-top: 16px; }

/* Milestones table */
.milestones { margin-top: 8px; }
.table-head { display: flex; align-items: center; gap: 10px; margin: 6px 0 8px; }
.table-head h2 { margin: 0; font-size: 18px; font-weight: 800; }
.mr-6 { margin-right: 6px; }

.subtitle { font-size: 16px; opacity: .92; margin: 0; }
.typing {
  display: inline-block; white-space: nowrap; overflow: hidden;
  border-right: .12em solid rgba(229,231,235,.65); width: 0;
  animation: typing 25s steps(60, end) 0s infinite, caret 700ms steps(1, end) infinite;
}
@keyframes typing { 0%{width:0} 32%{width:24%} 80%{width:24%} 100%{width:0} }
@keyframes caret { 50%{border-color:transparent} }

/* Drawer */
.el-table .is-highlight > td { background-color: #fff7e6 !important; }
.durian-table-wrapper .el-image { border: 1px solid #ebeef5; }
.addr {
  display: inline-block;
  word-break: break-all;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", monospace;
  user-select: text;
}

.detail-buttons .el-button + .el-button {
  margin-left: 0 !important; /* remove default left margin */
}

.detail-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 12px; /* use gap for spacing instead */
}
</style>
