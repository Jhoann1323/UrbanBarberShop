package com.barber.model;

import com.barber.model.Cita;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-09-11T01:42:00")
@StaticMetamodel(EstadoAsignacion.class)
public class EstadoAsignacion_ { 

    public static volatile SingularAttribute<EstadoAsignacion, String> descripcion;
    public static volatile SingularAttribute<EstadoAsignacion, Integer> idEstadoAsignacion;
    public static volatile ListAttribute<EstadoAsignacion, Cita> citaList;

}