"use client"

import { useForm } from 'react-hook-form';
import { z } from "zod";
import { zodResolver } from '@hookform/resolvers/zod';
import { toast } from 'sonner';
// import { Dialog, DialogContent, DialogTitle } from "@radix-ui/react-dialog";
import { Dialog, DialogContent, DialogTitle, DialogDescription, DialogHeader } from "../ui/dialog";
import { useCreatePatient } from '@/lib/api/patients';
import { CreatePatientRequest } from '@/lib/interfaces/patient';
import React from "react";
import {Label} from "@/components/ui/Label";
import {Input} from "@/components/ui/Input";
import {Button} from "@/components/ui/Button";

const patientSchema = z.object({
    firstName: z.string().min(1, 'First name is required'),
    lastName: z.string().min(1, 'Last name is required'),
    dateOfBirth: z.string().min(1, 'Date of birth is required'),
    gender: z.enum(['male', 'female', 'other']),
    phone: z.string().min(10, 'Valid phone number required'),
    email: z.string().email().optional().or(z.literal('')),
    address: z.object({
        line1: z.string().min(1, 'Address is required'),
        line2: z.string().optional(),
        city: z.string().min(1, 'City is required'),
        state: z.string().min(2, 'State is required'),
        zipCode: z.string().min(5, 'ZIP code is required'),
    }),
})

type PatientFormData = z.infer<typeof patientSchema>

interface CreatePatientDialogProps {
    open: boolean
    onClose: () => void
}

function CreatePatientDialog({ open, onClose }: CreatePatientDialogProps) {
    const createPatient = useCreatePatient();

    const {
        register,
        handleSubmit,
        reset,
        formState: { errors },
    } = useForm<PatientFormData>({
        resolver: zodResolver(patientSchema),
    });

    const onSubmit = async (data: PatientFormData) => {
        try {
            await createPatient.mutateAsync(data as CreatePatientRequest);
            toast.success("Patient created successfully");
            reset();
            onClose();
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
        } catch (error: any) {
            toast.error(error.response?.data.error?.message || "Failed to create patient");
        }
    }

    return (
        <Dialog open={open} onOpenChange={onClose}>
            <DialogContent className="border-border/50 bg-neutral-950/90 backdrop-blur-xl sm:max-w-[600px]">
                <DialogHeader>
                    <DialogTitle>Register New Patient</DialogTitle>
                </DialogHeader>

                <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <Label htmlFor="firstName">First Name *</Label>
                            <Input
                                id="firstName"
                                {...register('firstName')}
                                className={errors.firstName ? 'border-red-500' : ''}
                            />
                            {errors.firstName && (
                                <p className="text-sm text-red-500 mt-1">
                                    {errors.firstName.message}
                                </p>
                            )}
                        </div>
                        <div>
                            <Label htmlFor="lastName">Last Name *</Label>
                            <Input
                                id="lastName"
                                {...register('lastName')}
                                className={errors.lastName ? 'border-red-500' : ''}
                            />
                            {errors.lastName && (
                                <p className="text-sm text-red-500 mt-1">
                                    {errors.lastName.message}
                                </p>
                            )}
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <Label htmlFor="dateOfBirth">Date of Birth *</Label>
                            <Input
                                id="dateOfBirth"
                                type="date"
                                {...register('dateOfBirth')}
                                className={errors.dateOfBirth ? 'border-red-500' : ''}
                            />
                            {errors.dateOfBirth && (
                                <p className="text-sm text-red-500 mt-1">
                                    {errors.dateOfBirth.message}
                                </p>
                            )}
                        </div>

                        <div>
                            <Label htmlFor="gender">Gender *</Label>
                            <select
                                id="gender"
                                {...register('gender')}
                                className="w-full px-3 py-2 border rounded-md"
                            >
                                <option value="">Select gender</option>
                                <option value="male">Male</option>
                                <option value="female">Female</option>
                                <option value="other">Other</option>
                            </select>
                            {errors.gender && (
                                <p className="text-sm text-red-500 mt-1">
                                    {errors.gender.message}
                                </p>
                            )}
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <Label htmlFor="phone">Phone *</Label>
                            <Input
                                id="phone"
                                {...register('phone')}
                                placeholder="+1234567890"
                                className={errors.phone ? 'border-red-500' : ''}
                            />
                            {errors.phone && (
                                <p className="text-sm text-red-500 mt-1">
                                    {errors.phone.message}
                                </p>
                            )}
                        </div>

                        <div>
                            <Label htmlFor="email">Email</Label>
                            <Input
                                id="email"
                                type="email"
                                {...register('email')}
                                placeholder="patient@email.com"
                                className={errors.email ? 'border-red-500' : ''}
                            />
                            {errors.email && (
                                <p className="text-sm text-red-500 mt-1">
                                    {errors.email.message}
                                </p>
                            )}
                        </div>
                    </div>
                    <div>
                        <Label htmlFor="line1">Address Line 1 *</Label>
                        <Input
                            id="line1"
                            {...register('address.line1')}
                            placeholder="123 Main St"
                            className={errors.address?.line1 ? 'border-red-500' : ''}
                        />
                        {errors.address?.line1 && (
                            <p className="text-sm text-red-500 mt-1">
                                {errors.address.line1.message}
                            </p>
                        )}
                    </div>

                    <div>
                        <Label htmlFor="line2">Address Line 2</Label>
                        <Input
                            id="line2"
                            {...register('address.line2')}
                            placeholder="Apt 4B"
                        />
                    </div>

                    <div className="grid grid-cols-3 gap-4">
                        <div>
                            <Label htmlFor="city">City *</Label>
                            <Input
                                id="city"
                                {...register('address.city')}
                                className={errors.address?.city ? 'border-red-500' : ''}
                            />
                            {errors.address?.city && (
                                <p className="text-sm text-red-500 mt-1">
                                    {errors.address.city.message}
                                </p>
                            )}
                        </div>

                        <div>
                            <Label htmlFor="state">State *</Label>
                            <Input
                                id="state"
                                {...register('address.state')}
                                placeholder="IL"
                                className={errors.address?.state ? 'border-red-500' : ''}
                            />
                            {errors.address?.state && (
                                <p className="text-sm text-red-500 mt-1">
                                    {errors.address.state.message}
                                </p>
                            )}
                        </div>

                        <div>
                            <Label htmlFor="zipCode">ZIP Code *</Label>
                            <Input
                                id="zipCode"
                                {...register('address.zipCode')}
                                placeholder="62701"
                                className={errors.address?.zipCode ? 'border-red-500' : ''}
                            />
                            {errors.address?.zipCode && (
                                <p className="text-sm text-red-500 mt-1">
                                    {errors.address.zipCode.message}
                                </p>
                            )}
                        </div>
                    </div>
                    <div className="flex justify-end gap-2 pt-4">
                        <Button type="button" variant="outline" onClick={onClose}>
                            Cancel
                        </Button>
                        <Button type="submit" disabled={createPatient.isPending}>
                            {createPatient.isPending ? 'Creating...' : 'Create Patient'}
                        </Button>
                    </div>
                </form>
            </DialogContent>
        </Dialog>
    )
}

export default CreatePatientDialog
