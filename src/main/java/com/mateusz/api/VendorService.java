package com.mateusz.api;

import com.mateusz.model.Vendor;

import java.util.List;

public interface VendorService {
    boolean addVendor(Vendor vendor);
    void removeVendorByName(String vendorName);
    Vendor getVendorByName(String vendorName);
    List<Vendor> getAllVendors();
}
