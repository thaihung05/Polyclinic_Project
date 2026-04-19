/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pkdk.formatters;

import com.pkdk.pojo.Specialties;
import com.pkdk.service.SpecialtyService;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;

/**
 *
 * @author Admin
 */
public class SpecialtyFormatter implements Formatter<Specialties> {

    @Autowired
    private SpecialtyService specialtyService;

    @Override
    public String print(Specialties specialty, Locale locale) {
        return String.valueOf(specialty.getId());
    }

    @Override
    public Specialties parse(String text, Locale locale) throws ParseException {
        return this.specialtyService.getSpecialtyById(Integer.parseInt(text));
    }
}
