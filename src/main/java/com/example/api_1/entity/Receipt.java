package com.example.api_1.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity(name = "receipts")
@Getter
@Setter
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "receipt_seq")
    @SequenceGenerator(name = "receipt_seq", sequenceName = "inf_sys_el_shop.receipts_table_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "total_sum")
    private Double totalSum;

    @Column(name = "date_receipt")
    private Date DateReceipt;

    private Boolean paid;

    @ManyToOne
    @JoinColumn(name = "id_salesman", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Worker salesman;

    @Column(name = "id_salesman")
    private Integer idSalesman;

    @ManyToOne
    @JoinColumn(name = "id_buyer", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Buyer buyer;

    @Column(name = "id_buyer")
    private Integer idBuyer;

    @ManyToMany
    @JoinTable(
            name = "receipt_product",
            joinColumns = @JoinColumn(name = "id_receipt"),
            inverseJoinColumns = @JoinColumn(name = "id_product")
    )
    private List<Product> products;

    public boolean isPaid() {
        return paid;
    }

    public Object getDateReceipt() {
        return DateReceipt;
    }

    public Object getTotalSum() {
        return totalSum;
    }
}

