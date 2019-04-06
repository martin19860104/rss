package com.exemple.eac2_2017s1;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class XmlParser {
    private static final String ns = null;

    public List<Entrada> analitza(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return leerNoticias(parser);
        } finally {
            in.close();
        }
    }
    private List<Entrada> leerNoticias(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Entrada> listaItems = new ArrayList<Entrada>();
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, ns, "channel");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("item")) {
                listaItems.add(leerItem(parser));
            } else {
                saltar(parser);
            }
        }
        return listaItems;
    }

    private void saltar(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;


        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private Entrada leerItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        String titulo = null;
        String enlace = null;
        String autor = null;
        String descripcion = null;
        String fecha = null;
        String categoria = null;

        parser.require(XmlPullParser.START_TAG, ns, "item");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String etiqueta = parser.getName();
            if (etiqueta.equals("title")) { //
                titulo = leerEtiqueta(parser, "title");
            } else if (etiqueta.equals("media:description")) {
                descripcion = leerEtiqueta(parser, "media:description");
            } else if (etiqueta.equals("link")) {
                enlace = leerEtiqueta(parser, "link");
            } else if (etiqueta.equals("pubDate")) {
                fecha = leerEtiqueta(parser, "pubDate");
            } else if (etiqueta.equals("dc:creator")) {
                autor = leerEtiqueta(parser, "dc:creator");
            } else if (etiqueta.equals("category")) {
                if (categoria == null) {
                    categoria = (leerEtiqueta(parser, "category"));
                } else {
                    categoria += (", " + leerEtiqueta(parser, "category"));
                }
            }  else {
                saltar(parser);
            }
        }
        return new Entrada(titulo, descripcion, enlace, autor, fecha, categoria);
    }

    private String leerEtiqueta(XmlPullParser parser, String etiqueta) throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, ns, etiqueta);

        String contenido = llegeixText(parser);

        parser.require(XmlPullParser.END_TAG, ns, etiqueta);
        return contenido;
    }

    private String leerImagen(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "media:thumbnail");
        String imagen = parser.getAttributeValue(null, "url");
        parser.next();
        return imagen;
    }


    private String llegeixText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String resultat = "";

        if (parser.next() == XmlPullParser.TEXT) {
            resultat = parser.getText();
            parser.nextTag();
        }
        return resultat;
    }


    public static class Entrada implements Serializable {
        public final String titulo;
        public final String enlace;
        public final String autor;
        public final String descripcion;
        public final String fecha;
        public final String categoria;
//        public final String imagen;

        public Entrada(String titulo, String descripcion, String enlace, String autor, String fecha, String categoria) {
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.enlace = enlace;
            this.autor = autor;
            this.fecha = fecha;
            this.categoria = categoria;
//            this.imagen = imagen;
        }
    }

}