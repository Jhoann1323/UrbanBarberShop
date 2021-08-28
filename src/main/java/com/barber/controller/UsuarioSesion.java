/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barber.controller;

import com.barber.EJB.CiudadFacadeLocal;
import com.barber.EJB.TipoIdentificacionFacadeLocal;
import com.barber.EJB.TipoRolFacadeLocal;
import com.barber.EJB.TipoTelefonoFacadeLocal;
import com.barber.EJB.UsuarioFacadeLocal;
import com.barber.model.Ciudad;
import com.barber.model.TipoIdentificacion;
import com.barber.model.TipoRol;
import com.barber.model.TipoTelefono;
import com.barber.model.Usuario;
import com.barber.utilidades.Mail;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author juan
 */
@Named(value = "usuarioSesion")
@SessionScoped
public class UsuarioSesion implements Serializable {

    //Conexión con FacadeLocal's
    //Es un punto de conexión a la base de datos
    @EJB
    private UsuarioFacadeLocal usuarioFacadeLocal;
    //LLaves FK en FacadeLocal
    @EJB
    private CiudadFacadeLocal ciudadFacadeLocal;
    @EJB
    private TipoRolFacadeLocal rolFacadeLocal;
    @EJB
    private TipoTelefonoFacadeLocal tipoTelefonoFacadeLocal;
    @EJB
    private TipoIdentificacionFacadeLocal tipoIdentificacionFacadeLocal;

    private Usuario usuario;
    //Usar esta estructura para las FK
    @Inject
    private Ciudad ciudad;
    @Inject
    private TipoRol tipoRol;
    @Inject
    private TipoTelefono tipoTelefono;
    @Inject
    private TipoIdentificacion tipoIdentificacion;

    //Lista local
    private List<Usuario> usuarios;
    //Usar esta estructura para las FK (Listar)
    private List<TipoIdentificacion> tipoIdentificaciones;
    private List<TipoTelefono> tipoTelefonos;
    private List<TipoRol> roles;
    private List<Ciudad> ciudades;

    //Atributos de clase
    private String correoUsuario;
    private String contrasena;
    private String correoIn;
    private String claveIn;

    //------>Instacías de sesión<------
    private Usuario usuReg = new Usuario();
    private Usuario usuLog = new Usuario();
    private Usuario usuTemporal = new Usuario();
    //------>Instacías de sesión<------

    //Este código me permite mostrar los datos en un select de un formulario (Me lista los datos en la vista)
    @PostConstruct
    public void init() {
        //Usar esto para la estructura local
        usuarios = usuarioFacadeLocal.findAll();
        //Usar esta estructura para las FK
        ciudades = ciudadFacadeLocal.findAll();
        roles = rolFacadeLocal.findAll();
        tipoTelefonos = tipoTelefonoFacadeLocal.findAll();
        tipoIdentificaciones = tipoIdentificacionFacadeLocal.findAll();
        //Limpiar un formulario
        usuario = new Usuario();
    }
    
    //Login
    public void validarUsuario() throws IOException {
        usuLog = usuarioFacadeLocal.encontrarUsuarioCorreo(correoUsuario);
        //rol = ejbFacade.encontrarRol(numeroRol);
        if (usuLog != null) {
            if (usuLog.getCorreo().equals(correoUsuario)) {
                if (usuLog.getContrasena().equals(contrasena)) {
                    switch (usuLog.getTipoRolNumeroRol().toString()) {
                        case "Recepcionista":
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("correo", correoUsuario);
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Bienvenido!", "Bienvenido!"));
                            FacesContext.getCurrentInstance().getExternalContext().redirect("/UrbanBarberShop/faces/recepcionista/index.xhtml");
                        case "Cliente":
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("correo", correoUsuario);
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Bienvenido!", "Bienvenido!"));
                            FacesContext.getCurrentInstance().getExternalContext().redirect("/UrbanBarberShop/faces/cliente/index.xhtml");
                        case "Barbero":
                            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("correo", correoUsuario);
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Bienvenido!", "Bienvenido!"));
                            FacesContext.getCurrentInstance().getExternalContext().redirect("/UrbanBarberShop/faces/barbero/index.xhtml");
                        default:
                            break;
                    }
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "No dispones de un rol en el sistema", "No dispones de un rol en el sistema"));
                 }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Clave incorrecta", "Clave incorrecta"));
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "El usuario no existe", "El usuario no existe"));
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "El usuario no existe", "El usuario no existe"));
    }

    //Registrar usuario
    public void registrarUsuario() {
        try {
            //Usar esta estructura para las FK
            this.usuReg.setCiudadNumeroCiudad(ciudad);
            this.usuReg.setTipoRolNumeroRol(tipoRol);
            this.usuReg.setTipoIdentificacionIdTipoIdentificacion(tipoIdentificacion);
            this.usuReg.setTipoTelefonoNumeroTipoTelefono(tipoTelefono);
            //Principal
            usuarioFacadeLocal.create(usuReg);
            //Limpiar formulario de registro
            usuReg = new Usuario();
            //Encontrar datos
            usuarios = usuarioFacadeLocal.findAll();
        } catch (Exception e) {
        }
    }

    //Cerrar sesion
    public void cerrarSesion() throws IOException {
        //Se destruye la información almacenada en el FacesContext (Dentro del método validarUsuario())
        usuLog = null;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Has cerrado sesión", "Has cerrado sesión"));
        FacesContext.getCurrentInstance().getExternalContext().redirect("../index.xhtml");
    }

    //Recupera datos del usuario al cual se va a editar
    public void guardarTemporal(Usuario u) {
        usuTemporal = u;
    }

    //Editar usuario (En el modal)
    public void editarUsuario() {
        try {
            //usuTemporal sirve para el ciclo de vida de SOLO la edición
            //Estructura FK'S
            this.usuTemporal.setCiudadNumeroCiudad(ciudad);
            this.usuTemporal.setTipoRolNumeroRol(tipoRol);
            this.usuTemporal.setTipoIdentificacionIdTipoIdentificacion(tipoIdentificacion);
            this.usuTemporal.setTipoTelefonoNumeroTipoTelefono(tipoTelefono);
            //El parámetro que usea para editar es usuTemporal
            usuarioFacadeLocal.edit(usuTemporal);
            //Limpieza local
            usuTemporal = new Usuario();
            //Limpieza de las FK'S
            ciudad = new Ciudad();
            tipoRol = new TipoRol();
            tipoIdentificacion = new TipoIdentificacion();
            tipoTelefono = new TipoTelefono();
            usuarios = usuarioFacadeLocal.findAll();
            //Mensaje
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario modificado", "Usuario modificado"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error de edición", "Error de edición"));
        }
    }

    //Preparar página para eliminar
    public String prepararEliminar() {
        usuario = new Usuario();

        return "/.xhtml";
    }

    //Eliminar
    public void eliminarUsuario(Usuario u) {
        try {
            this.usuarioFacadeLocal.remove(u);
            //Colocar prepararEliminar()
            usuarios = usuarioFacadeLocal.findAll();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Usuario eliminado", "Usuario eliminado"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error al eliminar", "Error al eliminar"));
        }
    }

    public void recuperarClave() {
        try {
            usuReg = usuarioFacadeLocal.recuperarClave(correoIn);
            if (usuReg != null) {
                Mail.recuperarClaves(usuReg.getNombre(), usuReg.getCorreo(), usuReg.getContrasena());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correo enviado", "Correo enviado"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "No se encontró el correo", "No se encontró el correo"));
            }

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Exepción de correo", "Exepción de correo"));
        }

    }

    //Getters y Setters
    public Usuario getUsuReg() {
        return usuReg;
    }

    public void setUsuReg(Usuario usuReg) {
        this.usuReg = usuReg;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    public List<Ciudad> getCiudades() {
        return ciudades;
    }

    public void setCiudades(List<Ciudad> ciudades) {
        this.ciudades = ciudades;
    }

    public TipoRol getTipoRol() {
        return tipoRol;
    }

    public void setTipoRol(TipoRol tipoRol) {
        this.tipoRol = tipoRol;
    }

    public TipoTelefono getTipoTelefono() {
        return tipoTelefono;
    }

    public void setTipoTelefono(TipoTelefono tipoTelefono) {
        this.tipoTelefono = tipoTelefono;
    }

    public TipoIdentificacion getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(TipoIdentificacion tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public List<TipoIdentificacion> getTipoIdentificaciones() {
        return tipoIdentificaciones;
    }

    public void setTipoIdentificaciones(List<TipoIdentificacion> tipoIdentificaciones) {
        this.tipoIdentificaciones = tipoIdentificaciones;
    }

    public List<TipoTelefono> getTipoTelefonos() {
        return tipoTelefonos;
    }

    public void setTipoTelefonos(List<TipoTelefono> tipoTelefonos) {
        this.tipoTelefonos = tipoTelefonos;
    }

    public List<TipoRol> getRoles() {
        return roles;
    }

    public void setRoles(List<TipoRol> roles) {
        this.roles = roles;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuLog() {
        return usuLog;
    }

    public void setUsuLog(Usuario usuLog) {
        this.usuLog = usuLog;
    }

    public Usuario getUsuTemporal() {
        return usuTemporal;
    }

    public void setUsuTemporal(Usuario usuTemporal) {
        this.usuTemporal = usuTemporal;
    }

    public String getCorreoIn() {
        return correoIn;
    }

    public void setCorreoIn(String correoIn) {
        this.correoIn = correoIn;
    }

    public String getClaveIn() {
        return claveIn;
    }

    public void setClaveIn(String claveIn) {
        this.claveIn = claveIn;
    }

}
