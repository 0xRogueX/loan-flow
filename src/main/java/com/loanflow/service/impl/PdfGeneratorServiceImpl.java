package com.loanflow.service.impl;

import com.loanflow.service.PdfGeneratorService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.loanflow.entity.Payment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    @Override
    public byte[] generatePaymentReceipt(Payment payment) {
        // 1. Set up the document and memory stream
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // 2. Add Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("OFFICIAL PAYMENT RECEIPT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // 3. Add General Info (Receipt Number, Date)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
            document.add(new Paragraph("Receipt Number: " + payment.getReceiptNumber()));
            document.add(new Paragraph("Payment Date: " + payment.getPaidAt().format(formatter)));
            document.add(new Paragraph("Payment Mode: " + payment.getPaymentMode()));
            document.add(Chunk.NEWLINE);

            // 4. Create a Table for the Details
            PdfPTable table = new PdfPTable(2); // 2 columns
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Add Table Data
            addTableRow(table, "Borrower Name", payment.getBorrower().getName());
            addTableRow(table, "Borrower Email", payment.getBorrower().getEmail());
            addTableRow(table, "Loan Number", payment.getLoan().getLoanNumber());
            addTableRow(table, "EMI Installment Number", String.valueOf(payment.getEmiSchedule().getInstallmentNumber()));
            addTableRow(table, "Principal Paid", "$" + payment.getEmiSchedule().getPrincipalAmount());
            addTableRow(table, "Interest Paid", "$" + payment.getEmiSchedule().getInterestAmount());

            // Highlight Total Amount
            PdfPCell totalCellLabel = new PdfPCell(new Phrase("Total Amount Paid", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            PdfPCell totalCellValue = new PdfPCell(new Phrase("$" + payment.getPaidAmount(), FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            table.addCell(totalCellLabel);
            table.addCell(totalCellValue);

            document.add(table);

            // 5. Add Footer
            Paragraph footer = new Paragraph("Thank you for your payment. If you have any questions, contact support@loanflow.com.");
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30);
            document.add(footer);

        } finally {
            document.close();
        }

        // Return the raw PDF bytes
        return out.toByteArray();
    }

    private void addTableRow(PdfPTable table, String key, String value) {
        PdfPCell cell1 = new PdfPCell(new Phrase(key));
        cell1.setPadding(8f);
        PdfPCell cell2 = new PdfPCell(new Phrase(value));
        cell2.setPadding(8f);
        table.addCell(cell1);
        table.addCell(cell2);
    }

}
