package com.grade.quickid.model.registros.aplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.Result;
import com.grade.quickid.model.Time;
import com.grade.quickid.model.eventos.domain.Evento;
import com.grade.quickid.model.registros.domain.Registro;

public class CrearRegistro {

    public Object CrearObjetoRegistro(Evento evento, Result result, String actPer, String idRegistro) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String nombreEvento = evento.getNombre();
        String lugarEvento = evento.getLugar();
        String idEvento = evento.getIdEvento();
        String imagenUrl = evento.getUrlImagen();
        Time time = new Time();
        // En este momento el usuario toma una copia
        // y se crea un nuevo registro
        // tengo que validar que en la misma fecha no se registre mas de una vez
        // o llevar por parametro las veces que se necesita tomar asistencia
        Registro registro = new Registro();
        registro.setIdRegistro(idRegistro);
        registro.setNombreEvento(nombreEvento);
        registro.setLugarEvento(lugarEvento);
        registro.setIdEvento(idEvento);
        registro.setIdPersona(user.getUid());
        registro.setHoraRegistro(time.hora());
        registro.setFechaRegistro(time.fecha());
        registro.setImagenEvento(imagenUrl);
        registro.setVisibilidad("1");
        registro.setIdAct_idPer(actPer);
        //decision
        return registro;
    }
}
