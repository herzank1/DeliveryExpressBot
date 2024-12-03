/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.deliveryexpress.utils;

import com.monge.tbotboot.messenger.MessageMenu;
import java.util.ArrayList;
import java.util.List;


/*
 * Estructura una lista en forma de libro, dividiendo los elementos de la lista
 * en secciones o páginas.
 *
 * @param <T> Tipo de los elementos de la lista
 */
public class PageListViewer<T> {

    private final int maxItemsPerPage;
    private final ArrayList<T> list;

    public PageListViewer(ArrayList<T> list, int maxItemsPerPage) {
        if (list == null || maxItemsPerPage <= 0) {
            throw new IllegalArgumentException("La lista no puede ser nula y maxItemsPerPage debe ser mayor a 0.");
        }
        this.maxItemsPerPage = maxItemsPerPage;
        this.list = list;
    }

    /**
     * @return El número total de elementos en la lista.
     */
    public int itemsCount() {
        return list.size();
    }

    /**
     * @return El número total de páginas necesarias para mostrar todos los
     * elementos.
     */
    public int pageCount() {
        return (int) Math.ceil((double) list.size() / maxItemsPerPage);
    }

    /**
     * Calcula el índice inicial de una página (0-based).
     *
     * @param page Número de la página (1-based)
     * @return Índice inicial de la página.
     */
    public int getStart(int page) {
        validatePageNumber(page);
        return (page - 1) * maxItemsPerPage;
    }

    /**
     * Calcula el índice final de una página (0-based, exclusivo).
     *
     * @param page Número de la página (1-based)
     * @return Índice final (exclusivo) de la página.
     */
    public int getEnd(int page) {
        validatePageNumber(page);
        return Math.min(page * maxItemsPerPage, list.size());
    }

    /**
     * Obtiene los elementos de una página específica.
     *
     * @param pagenumber Número de la página (1-based).
     * @return Una lista con los elementos de la página solicitada.
     */
    public ArrayList<T> getPage(int pagenumber) {
        if (pagenumber <= 0 || pagenumber > pageCount()) {
            return new ArrayList<>();
        }
        int start = getStart(pagenumber);
        int end = getEnd(pagenumber);
        return new ArrayList<>(list.subList(start, end));
    }

    /**
     * Obtiene los elementos de la página anterior.
     *
     * @param current Número de la página actual (1-based).
     * @return Una lista con los elementos de la página anterior.
     */
    public ArrayList<T> getPrevPage(int current) {
        return getPage(current - 1);
    }

    /**
     * Obtiene los elementos de la página siguiente.
     *
     * @param current Número de la página actual (1-based).
     * @return Una lista con los elementos de la página siguiente.
     */
    public ArrayList<T> getNextPage(int current) {
        return getPage(current + 1);
    }

    /**
     * @return El número máximo de elementos por página.
     */
    public int getItemsPerPage() {
        return this.maxItemsPerPage;
    }

    /**
     * Verifica si hay una página siguiente al número de página actual.
     *
     * @param pageNumber Número de la página actual (1-based).
     * @return `true` si hay una página siguiente, `false` en caso contrario.
     */
    public boolean hasNext(int pageNumber) {
        return pageNumber > 0 && pageNumber < pageCount();
    }

    /**
     * Verifica si hay una página anterior al número de página actual.
     *
     * @param pageNumber Número de la página actual (1-based).
     * @return `true` si hay una página anterior, `false` en caso contrario.
     */
    public boolean hasPrevious(int pageNumber) {
        return pageNumber > 1 && pageNumber <= pageCount();
    }

    /**
     * Valida que el número de página esté dentro del rango válido.
     *
     * @param page Número de la página (1-based)
     */
    private void validatePageNumber(int page) {
        if (page <= 0 || page > pageCount()) {
            throw new IllegalArgumentException("El número de página no es válido: " + page);
        }
    }

    @Override
    public String toString() {
        return "PageListViewer{" + "maxItemsPerPage=" + maxItemsPerPage
                + ", size=" + this.list.size() + '}';
    }

    public int getValueOf(String page) {

        if (page == null || page.isEmpty()) {
            return 1;
        }

        try {
            Integer valueOf = Integer.valueOf(page);
            if (valueOf > this.pageCount()) {
                return 1;
            } else {
                return valueOf;
            }
        } catch (NumberFormatException e) {

            return 1;
        }

    }

    /**Regresa el menu de botones correspondiente a la pagina*/
    public MessageMenu getNavMenu(int page) {
        MessageMenu menu = new MessageMenu();

        if (this.hasPrevious(page)) {
            menu.addButton("⬅", "/myorders&" + (page - 1));
        }

        if (this.hasNext(page)) {
            menu.addButton("➡", "/myorders&" + (page + 1));
        }
        
        return menu;

    }

}
