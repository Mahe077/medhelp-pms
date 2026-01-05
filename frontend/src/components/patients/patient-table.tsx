import { Patient } from "@/lib/interfaces/patient";
import { Link } from "lucide-react";
import { format } from "path";
import { Button } from "../ui/Button";
import { Table, TableHeader, TableRow, TableHead, TableBody, TableCell } from "../ui/table";
import { Card } from "../ui/Card";

interface PatientTableProps {
    patients: Patient[];
    pagination?: {
        page: number;
        pageSize: number;
        totalPages: number;
        totalItems: number;
    };
    onPageChange: (page: number) => void;
}

export function PatientTable({
    patients,
    pagination,
    onPageChange,
}: PatientTableProps) {
    return (
        <Card className="border-border/50 ">
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead>Patient #</TableHead>
                        <TableHead>Name</TableHead>
                        <TableHead>Date of Birth</TableHead>
                        <TableHead>Phone</TableHead>
                        <TableHead>Email</TableHead>
                        <TableHead>Actions</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {patients.map((patient) => (
                        <TableRow key={patient.id}>
                            <TableCell className="font-medium">
                                {patient.patientNumber}
                            </TableCell>
                            <TableCell>
                                {patient.firstName} {patient.lastName}
                            </TableCell>
                            <TableCell>
                                {new Date(patient.dateOfBirth).toLocaleDateString()}
                            </TableCell>
                            <TableCell>{patient.phone}</TableCell>
                            <TableCell>{patient.email || '-'}</TableCell>
                            <TableCell>
                                <Link href={`/patients/${patient.id}`}>
                                    <Button variant="ghost" size="sm">
                                        View
                                    </Button>
                                </Link>
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>

            {pagination && pagination.totalPages > 1 && (
                <div className="flex items-center justify-between px-4 py-3 border-t">
                    <div className="text-sm text-gray-500">
                        Showing {(pagination.page - 1) * pagination.pageSize + 1} to{' '}
                        {Math.min(
                            pagination.page * pagination.pageSize,
                            pagination.totalItems
                        )}{' '}
                        of {pagination.totalItems} results
                    </div>
                    <div className="flex gap-2">
                        <Button
                            variant="outline"
                            size="sm"
                            onClick={() => onPageChange(pagination.page - 1)}
                            disabled={pagination.page === 1}
                        >
                            Previous
                        </Button>
                        <Button
                            variant="outline"
                            size="sm"
                            onClick={() => onPageChange(pagination.page + 1)}
                            disabled={pagination.page === pagination.totalPages}
                        >
                            Next
                        </Button>
                    </div>
                </div>
            )}
        </Card>
    );
}
