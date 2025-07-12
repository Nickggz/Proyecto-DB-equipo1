import React, { useState } from 'react';

const Auth = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [cedula, setCedula] = useState('');
  const [nombre, setNombre] = useState('');
  const [email, setEmail] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);

  const API_URL = 'http://localhost:8080/api';

  // Validación de cédula uruguaya
  const validarCedulaUruguaya = (cedula) => {
    // Remover puntos y guiones
    const cedulaLimpia = cedula.replace(/[.-]/g, '');
    
    // Verificar que tenga 8 dígitos
    if (!/^\d{8}$/.test(cedulaLimpia)) {
      return false;
    }

    // Algoritmo de verificación de cédula uruguaya
    const digitos = cedulaLimpia.split('').map(Number);
    const verificador = digitos.pop();
    
    const coeficientes = [2, 9, 8, 7, 6, 3, 4];
    let suma = 0;
    
    for (let i = 0; i < 7; i++) {
      suma += digitos[i] * coeficientes[i];
    }
    
    const resto = suma % 10;
    const digitoVerificador = resto === 0 ? 0 : 10 - resto;
    
    return digitoVerificador === verificador;
  };

  // Formatear cédula con puntos y guión
  const formatearCedula = (value) => {
    // Remover caracteres no numéricos
    const numeros = value.replace(/\D/g, '');
    
    // Limitar a 8 dígitos
    if (numeros.length > 8) return cedula;
    
    // Formatear: X.XXX.XXX-X
    if (numeros.length <= 1) return numeros;
    if (numeros.length <= 4) return `${numeros.slice(0, 1)}.${numeros.slice(1)}`;
    if (numeros.length <= 7) return `${numeros.slice(0, 1)}.${numeros.slice(1, 4)}.${numeros.slice(4)}`;
    return `${numeros.slice(0, 1)}.${numeros.slice(1, 4)}.${numeros.slice(4, 7)}-${numeros.slice(7)}`;
  };

  const handleCedulaChange = (e) => {
    const formateada = formatearCedula(e.target.value);
    setCedula(formateada);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage('');

    // Validar cédula
    if (!validarCedulaUruguaya(cedula)) {
      setMessage('Cédula uruguaya inválida');
      setLoading(false);
      return;
    }

    try {
      const endpoint = isLogin ? '/login' : '/register';
      const data = isLogin 
        ? { cedula: cedula.replace(/[.-]/g, '') }
        : { 
            cedula: cedula.replace(/[.-]/g, ''), 
            nombre: nombre.trim(),
            email: email.trim()
          };

      // Llamada real al backend con fetch
      const response = await fetch(`${API_URL}${endpoint}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
      });
      
      const result = await response.json();
      const responseObj = { data: result };
      
      if (responseObj.data.success) {
        setMessage(isLogin ? 
          `¡Bienvenido, ${responseObj.data.nombre}!` : 
          '¡Registro exitoso! Ya puedes iniciar sesión.'
        );
        
        // Limpiar formulario después de registro exitoso
        if (!isLogin) {
          setCedula('');
          setNombre('');
          setEmail('');
          setIsLogin(true);
        }
      } else {
        setMessage(responseObj.data.message || 'Error en la operación');
      }
    } catch (error) {
      setMessage('Error de conexión. Verifica que el backend esté corriendo en el puerto 8080');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container" style={{
      minHeight: '100vh',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      backgroundColor: '#f5f5f5',
      padding: '20px'
    }}>
      <div className="auth-card" style={{
        backgroundColor: 'white',
        padding: '40px',
        borderRadius: '12px',
        boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
        width: '100%',
        maxWidth: '450px'
      }}>
        <div className="auth-header" style={{ textAlign: 'center', marginBottom: '30px' }}>
          <h2 style={{ color: '#333', marginBottom: '8px' }}>
            {isLogin ? 'Iniciar Sesión' : 'Registrarse'}
          </h2>
          <p style={{ color: '#666', margin: '0' }}>
            Sistema de autenticación con cédula uruguaya
          </p>
        </div>

        <div className="auth-tabs" style={{ 
          display: 'flex', 
          marginBottom: '30px',
          borderBottom: '1px solid #eee'
        }}>
          <button 
            className={isLogin ? 'tab active' : 'tab'}
            onClick={() => {
              setIsLogin(true);
              setMessage('');
              setCedula('');
              setNombre('');
              setEmail('');
            }}
            style={{
              flex: 1,
              padding: '12px',
              border: 'none',
              backgroundColor: 'transparent',
              color: isLogin ? '#007bff' : '#666',
              borderBottom: isLogin ? '2px solid #007bff' : '2px solid transparent',
              cursor: 'pointer',
              fontSize: '16px'
            }}
          >
            Iniciar Sesión
          </button>
          <button 
            className={!isLogin ? 'tab active' : 'tab'}
            onClick={() => {
              setIsLogin(false);
              setMessage('');
              setCedula('');
              setNombre('');
              setEmail('');
            }}
            style={{
              flex: 1,
              padding: '12px',
              border: 'none',
              backgroundColor: 'transparent',
              color: !isLogin ? '#007bff' : '#666',
              borderBottom: !isLogin ? '2px solid #007bff' : '2px solid transparent',
              cursor: 'pointer',
              fontSize: '16px'
            }}
          >
            Registrarse
          </button>
        </div>

        <div onSubmit={handleSubmit} className="auth-form">
          <div className="form-group" style={{ marginBottom: '20px' }}>
            <label htmlFor="cedula" style={{ 
              display: 'block', 
              marginBottom: '8px', 
              color: '#333',
              fontSize: '14px',
              fontWeight: '500'
            }}>
              Cédula de Identidad
            </label>
            <input
              type="text"
              id="cedula"
              value={cedula}
              onChange={handleCedulaChange}
              placeholder="1.234.567-8"
              required
              className="form-input"
              style={{
                width: '100%',
                padding: '12px',
                border: '1px solid #ddd',
                borderRadius: '6px',
                fontSize: '16px'
              }}
            />
            <small className="form-hint" style={{ 
              color: '#666', 
              fontSize: '12px',
              marginTop: '4px',
              display: 'block'
            }}>
              Formato: X.XXX.XXX-X
            </small>
          </div>

          {!isLogin && (
            <>
              <div className="form-group" style={{ marginBottom: '20px' }}>
                <label htmlFor="nombre" style={{ 
                  display: 'block', 
                  marginBottom: '8px', 
                  color: '#333',
                  fontSize: '14px',
                  fontWeight: '500'
                }}>
                  Nombre Completo
                </label>
                <input
                  type="text"
                  id="nombre"
                  value={nombre}
                  onChange={(e) => setNombre(e.target.value)}
                  placeholder="Tu nombre completo"
                  required
                  className="form-input"
                  style={{
                    width: '100%',
                    padding: '12px',
                    border: '1px solid #ddd',
                    borderRadius: '6px',
                    fontSize: '16px'
                  }}
                />
              </div>

              <div className="form-group" style={{ marginBottom: '20px' }}>
                <label htmlFor="email" style={{ 
                  display: 'block', 
                  marginBottom: '8px', 
                  color: '#333',
                  fontSize: '14px',
                  fontWeight: '500'
                }}>
                  Email
                </label>
                <input
                  type="email"
                  id="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="tu@email.com"
                  required
                  className="form-input"
                  style={{
                    width: '100%',
                    padding: '12px',
                    border: '1px solid #ddd',
                    borderRadius: '6px',
                    fontSize: '16px'
                  }}
                />
              </div>
            </>
          )}

          <button 
            type="submit" 
            disabled={loading}
            className="submit-button"
            onClick={handleSubmit}
            style={{
              width: '100%',
              padding: '14px',
              backgroundColor: loading ? '#ccc' : '#007bff',
              color: 'white',
              border: 'none',
              borderRadius: '6px',
              fontSize: '16px',
              cursor: loading ? 'not-allowed' : 'pointer',
              marginTop: '20px'
            }}
          >
            {loading ? 'Procesando...' : (isLogin ? 'Iniciar Sesión' : 'Registrarse')}
          </button>
        </div>

        {message && (
          <div className={`message ${message.includes('Error') || message.includes('inválida') ? 'error' : 'success'}`}
               style={{
                 padding: '12px',
                 borderRadius: '6px',
                 marginTop: '20px',
                 backgroundColor: message.includes('Error') || message.includes('inválida') ? '#fee' : '#efe',
                 color: message.includes('Error') || message.includes('inválida') ? '#c33' : '#060',
                 border: `1px solid ${message.includes('Error') || message.includes('inválida') ? '#fcc' : '#cfc'}`
               }}>
            {message}
          </div>
        )}

        <div className="auth-info" style={{
          marginTop: '30px',
          padding: '20px',
          backgroundColor: '#f8f9fa',
          borderRadius: '8px',
          border: '1px solid #e9ecef'
        }}>
          <h3 style={{ color: '#333', marginBottom: '12px', fontSize: '16px' }}>
            Información sobre cédulas uruguayas:
          </h3>
          <ul style={{ color: '#666', fontSize: '14px', lineHeight: '1.5' }}>
            <li>Formato: X.XXX.XXX-X (8 dígitos)</li>
            <li>Incluye dígito verificador</li>
            <li>Ejemplos válidos: 1.234.567-8, 4.567.890-1</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default Auth;