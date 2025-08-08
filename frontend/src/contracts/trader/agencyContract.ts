import { getContract } from '@/contracts/web3'

/**
 * Register a new trader agency
 */
export async function createTraderAgency(
    agencyId: string,
    agencyName: string,
    exportLicenseCID: string,
    exportLicenseExpiry: number
): Promise<string> {
    const contract = await getContract()
    const tx = await contract.createTraderAgency(
        agencyId,
        agencyName,
        exportLicenseCID,
        exportLicenseExpiry
    )
    await tx.wait()
    return tx.hash
}

/**
 * Update export license for a trader agency
 */
export async function updateTraderAgencyCertificate(
    agencyId: string,
    newExportCID: string,
    newExportExpiry: number
): Promise<string> {
    const contract = await getContract()
    const tx = await contract.updateTraderAgencyCertificate(
        agencyId,
        newExportCID,
        newExportExpiry
    )
    await tx.wait()
    return tx.hash
}

/**
 * Get all trader agency IDs owned by the connected wallet
 */
export async function getMyTraderAgencyIds(): Promise<string[]> {
    const contract = await getContract()
    return await contract.getMyTraderAgencyIds()
}

/**
 * Get trader agency info by agency ID
 */
export async function getTraderAgencyById(agencyId: string): Promise<{
    agencyId: string
    agencyName: string
    owner: string
    exportLicenseCID: string
    exportLicenseExpiry: string
}> {
    const contract = await getContract()
    const result = await contract.getTraderAgencyById(agencyId)

    return {
        agencyId: result[0],
        agencyName: result[1],
        owner: result[2],
        exportLicenseCID: result[3],
        exportLicenseExpiry: result[4].toString(),
    }
}

/**
 * Get agency milestone logs by agency ID
 */
export async function getTraderAgencyMilestone(agencyId: string): Promise<
    {
        action: string
        description: string
        actor: string
        role: string
        timestamp: string
    }[]
> {
    const contract = await getContract()
    const [actions, descriptions, actors, roleNames, timestamps] =
        await contract.getTraderAgencyMilestone(agencyId)

    return actions.map((_, i) => ({
        action: actions[i],
        description: descriptions[i],
        actor: actors[i],
        role: roleNames[i],
        timestamp: timestamps[i].toString(),
    }))
}
