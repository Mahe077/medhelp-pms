"use client"

import { Button } from '@/components/ui/Button'
import { PlusCircle } from 'lucide-react';
import { useState } from 'react';
import { usePatients } from '@/lib/api/patients';
import CreatePatientDialog from '@/components/patients/create-patient-dialog';
import { PatientTable } from '@/components/patients/patient-table';

function MedicationsPage() {
    const [search, setSearch] = useState('');
    const [page, setPage] = useState(1);
    const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);

    const { data, isLoading } = usePatients({
        search,
        page,
        pageSize: 20,
    });

    return (
        <div className="space-y-6">
            <div className={"flex items-center justify-between"}>
                <div className="flex flex-col gap-2">
                    <h1 className="text-3xl font-bold tracking-tight">Medications</h1>
                    <p className="text-muted-foreground">
                        Manage medications records and interactions.
                    </p>
                </div>

                <Button className="gap-2 shadow-lg shadow-primary/20" onClick={() => setIsCreateDialogOpen(true)}>
                    <PlusCircle className="h-4 w-4" />
                    New Medication
                </Button>
            </div>

            {isLoading ? (
                <div className="text-center py-8">Loading...</div>
            ) : (
                <PatientTable
                    patients={data?.data || []}
                    pagination={data?.pagination}
                    onPageChange={setPage}
                />
            )}

            <CreatePatientDialog
                open={isCreateDialogOpen}
                onClose={() => setIsCreateDialogOpen(false)}
            />
        </div>
    )
}

export default MedicationsPage

