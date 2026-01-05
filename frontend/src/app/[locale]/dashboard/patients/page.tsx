"use client"

import { Button } from '@/components/ui/Button'
import { Card } from '@/components/ui/Card';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Input } from '@/components/ui/Input';
import { Label } from '@/components/ui/Label';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { PlusCircle, Printer } from 'lucide-react';
import React, { useMemo, useState } from 'react';
import { Patient } from '@/lib/interfaces/patient';
import { usePatients } from '@/lib/api/patients';
import CreatePatientDialog from '@/components/patients/create-patient-dialog';
import { PatientTable } from '@/components/patients/patient-table';


function PatientsPage() {
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
                    <h1 className="text-3xl font-bold tracking-tight">Patients</h1>
                    <p className="text-muted-foreground">
                        Manage patient records and interactions.
                    </p>
                </div>

                <Button className="gap-2 shadow-lg shadow-primary/20" onClick={() => setIsCreateDialogOpen(true)}>
                    <PlusCircle className="h-4 w-4" />
                    New Patient
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

export default PatientsPage

