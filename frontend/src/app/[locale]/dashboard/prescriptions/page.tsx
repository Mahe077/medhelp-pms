"use client"

import React, {useState} from 'react'
import {Card} from "@/components/ui/Card";
import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@/components/ui/table";
import {Button} from "@/components/ui/Button";
import {Check, PlusCircle, Printer} from "lucide-react";
import {Badge} from "@/components/ui/badge";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from "@/components/ui/dialog";
import {Label} from "@/components/ui/Label";
import {Input} from "@/components/ui/Input";

const prescriptions = [
    {
        id: "RX-82731",
        patient: "John Doe",
        medication: "Amoxicillin 500mg",
        date: "2024-01-15",
        refills: "2/3",
        status: "ready",
    },
    {
        id: "RX-82732",
        patient: "Jane Smith",
        medication: "Lisinopril 10mg",
        date: "2024-01-15",
        refills: "1/2",
        status: "processing",
    },
    {
        id: "RX-82733",
        patient: "Robert Brown",
        medication: "Metformin 850mg",
        date: "2024-01-14",
        refills: "0/3",
        status: "completed",
    },
]

export default function PrescriptionPage() {
    const [open, setOpen] = useState(false)
    const [step, setStep] = useState(1)

    const handleNext = () => {
        if (step < 3) setStep(step + 1)
        else {
            setOpen(false)
            setStep(1)
        }
    }

    return (
        <div className="space-y-6">
            <div className={"flex items-center justify-between"}>
                <div className="flex flex-col gap-2">
                    <h1 className="text-3xl font-bold tracking-tight">Prescriptions</h1>
                    <p className="text-muted-foreground">
                        Process, verify, and track medication orders.
                    </p>
                </div>

                <Dialog open={open} onOpenChange={setOpen}>
                    <DialogTrigger asChild>
                        <Button className="gap-2 shadow-lg shadow-primary/20">
                            <PlusCircle className="h-4 w-4" />
                            New Prescription
                        </Button>
                    </DialogTrigger>
                    <DialogContent className="border-border/50 bg-neutral-950/90 backdrop-blur-xl sm:max-w-[600px]">
                        <DialogHeader>
                            <DialogTitle>New Prescription Entry</DialogTitle>
                            <DialogDescription>
                                Step {step} of 3:{" "}
                                {step === 1 ? "Patient & Medication" : step === 2 ? "Dosage Information" : "Verification"}
                            </DialogDescription>
                        </DialogHeader>
                        <div className="grid gap-4 py-4">
                            {step === 1 && (
                                <>
                                    <div className="grid gap-2">
                                        <Label htmlFor="patient">Patient Name</Label>
                                        <Input id="patient" placeholder="Search or select patient" className="bg-background/50" />
                                    </div>
                                    <div className="grid gap-2">
                                        <Label htmlFor="medication">Medication</Label>
                                        <Input id="medication" placeholder="Search medication" className="bg-background/50" />
                                    </div>
                                    <div className="grid grid-cols-2 gap-4">
                                        <div className="grid gap-2">
                                            <Label htmlFor="doctor">Prescribing Doctor</Label>
                                            <Input id="doctor" placeholder="Dr. Name" className="bg-background/50" />
                                        </div>
                                        <div className="grid gap-2">
                                            <Label htmlFor="dea">DEA Number</Label>
                                            <Input id="dea" placeholder="DEA-XXXXXXX" className="bg-background/50" />
                                        </div>
                                    </div>
                                </>
                            )}
                            {step === 2 && (
                                <>
                                    <div className="grid grid-cols-3 gap-4">
                                        <div className="grid gap-2">
                                            <Label htmlFor="quantity">Quantity</Label>
                                            <Input id="quantity" type="number" placeholder="30" className="bg-background/50" />
                                        </div>
                                        <div className="grid gap-2">
                                            <Label htmlFor="refills">Refills</Label>
                                            <Input id="refills" type="number" placeholder="2" className="bg-background/50" />
                                        </div>
                                        <div className="grid gap-2">
                                            <Label htmlFor="days">Days Supply</Label>
                                            <Input id="days" type="number" placeholder="30" className="bg-background/50" />
                                        </div>
                                    </div>
                                    <div className="grid gap-2">
                                        <Label htmlFor="sig">SIG (Patient Instructions)</Label>
                                        <Input id="sig" placeholder="Take 1 tablet by mouth daily" className="bg-background/50" />
                                    </div>
                                </>
                            )}
                            {step === 3 && (
                                <div className="space-y-4 rounded-lg border border-border/50 bg-muted/20 p-4">
                                    <div className="flex items-center gap-2 text-emerald-500">
                                        <Check className="h-5 w-5" />
                                        <span className="font-semibold">Prescription verified and ready to finalize</span>
                                    </div>
                                    <div className="space-y-2 text-sm text-muted-foreground">
                                        <p>Patient: John Doe (P001)</p>
                                        <p>Medication: Amoxicillin 500mg</p>
                                        <p>Quantity: 30 tablets, Refills: 2</p>
                                    </div>
                                </div>
                            )}
                        </div>
                        <DialogFooter>
                            {step > 1 && (
                                <Button variant="outline" onClick={() => setStep(step - 1)}>
                                    Back
                                </Button>
                            )}
                            <Button onClick={handleNext}>{step === 3 ? "Finalize Rx" : "Next"}</Button>
                        </DialogFooter>
                    </DialogContent>
                </Dialog>
            </div>

            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                <div className="rounded-xl border border-border bg-card p-6 shadow-sm">
                    <div className="flex flex-col gap-1">
                        <span className="text-xs font-medium text-muted-foreground uppercase">Total Sales</span>
                        <div className="flex items-baseline gap-2">
                            <span className="text-2xl font-bold">Rs. 124,500</span>
                            <span className="text-xs font-medium text-green-500">+12%</span>
                        </div>
                    </div>
                </div>
                <div className="rounded-xl border border-border bg-card p-6 shadow-sm">
                    <div className="flex flex-col gap-1">
                        <span className="text-xs font-medium text-muted-foreground uppercase">Prescriptions</span>
                        <div className="flex items-baseline gap-2">
                            <span className="text-2xl font-bold">48</span>
                            <span className="text-xs font-medium text-green-500">+5</span>
                        </div>
                    </div>
                </div>
                <div className="rounded-xl border border-border bg-card p-6 shadow-sm">
                    <div className="flex flex-col gap-1">
                        <span className="text-xs font-medium text-muted-foreground uppercase">Active Patients</span>
                        <div className="flex items-baseline gap-2">
                            <span className="text-2xl font-bold">1,240</span>
                            <span className="text-xs font-medium text-blue-500">Stable</span>
                        </div>
                    </div>
                </div>
                <div className="rounded-xl border border-border bg-card p-6 shadow-sm">
                    <div className="flex flex-col gap-1">
                        <span className="text-xs font-medium text-muted-foreground uppercase">Low Stock Icons</span>
                        <div className="flex items-baseline gap-2">
                            <span className="text-2xl font-bold">12</span>
                            <span className="text-xs font-medium text-destructive">Action Required</span>
                        </div>
                    </div>
                </div>
            </div>

            <Card className="border-border/50 ">
                <Table>
                    <TableHeader>
                        <TableHead className="text-muted-foreground">Rx Number</TableHead>
                        <TableHead className="text-muted-foreground">Patient</TableHead>
                        <TableHead className="text-muted-foreground">Medication</TableHead>
                        <TableHead className="text-muted-foreground">Date</TableHead>
                        <TableHead className="text-muted-foreground">Refills</TableHead>
                        <TableHead className="text-muted-foreground">Status</TableHead>
                        <TableHead className="text-right text-muted-foreground">Actions</TableHead>
                    </TableHeader>
                    <TableBody>
                        {prescriptions.map((rx) => (
                            <TableRow key={rx.id} className="border-border/50">
                                <TableCell className="font-mono text-xs font-semibold text-primary">{rx.id}</TableCell>
                                <TableCell className="font-semibold text-foreground">{rx.patient}</TableCell>
                                <TableCell className="text-muted-foreground">{rx.medication}</TableCell>
                                <TableCell className="text-muted-foreground">{rx.date}</TableCell>
                                <TableCell className="text-sm text-muted-foreground">{rx.refills}</TableCell>
                                <TableCell>
                                    <Badge
                                        variant={rx.status === "ready" ? "default" : rx.status === "processing" ? "secondary" : "outline"}
                                        className="text-xs capitalize"
                                    >
                                        {rx.status}
                                    </Badge>
                                </TableCell>
                                <TableCell className="text-right">
                                    <Button variant="ghost" size="sm" className="gap-2">
                                        <Printer className="h-3.5 w-3.5" />
                                        Label
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </Card>
        </div>
    );
}