package tn.esprit.service;
import java.sql.SQLException;
import tn.esprit.model.Cheque;
import tn.esprit.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ChequeService implements IICRUD <Cheque> {
    private Connection cnx;


    public ChequeService(){
        cnx = DBConnection.getInstance().getConnection();




    }
    @Override
    public void create(Cheque cheque) throws SQLException, java.sql.SQLIntegrityConstraintViolationException {
        String sql = "INSERT INTO Cheque (num, numcompte,montant,date) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = cnx.prepareStatement(sql);




        statement.setLong(1, cheque.getNum());
        statement.setInt(2, cheque.getNumcompte());
        statement.setFloat(3, cheque.getMontant());
        statement.setDate(4, new java.sql.Date(cheque.getDate().getTime()));




        statement.executeUpdate();
    }
    @Override
    public void update(Cheque cheque) throws SQLException {
        String sql = "UPDATE Cheque SET num = ?, numcompte = ?, montant = ?, date = ? WHERE id = ?";
        PreparedStatement ps = null;
        try {
            ps = cnx.prepareStatement(sql);
            ps.setLong(1, cheque.getNum());
            ps.setInt(2, cheque.getNumcompte());
            ps.setFloat(3, cheque.getMontant());
            ps.setDate(4, new java.sql.Date(cheque.getDate().getTime()));
            ps.setInt(5, cheque.getId());
            ps.executeUpdate();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Cheque WHERE id = ?";
        try (PreparedStatement preparedStatement = cnx.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }
    @Override
    public List<Cheque> read() throws SQLException {
        String sql = "SELECT * FROM Cheque";
        Statement statement = cnx.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        List<Cheque> cheques = new ArrayList<>();
        while (rs.next()) {
            Cheque cheque = new Cheque();
            cheque.setId(rs.getInt("id"));
            cheque.setNum(rs.getInt("Num"));
            cheque.setNumcompte(rs.getInt("Numcompte"));
            cheque.setMontant(rs.getFloat("montant"));
            cheque.setDate(rs.getDate("date"));
            cheques.add(cheque);
        }
        return cheques;
    }
    public Cheque readCarte(int chequeId) throws SQLException {
        String sql = "SELECT * FROM Cheque WHERE id = ?";
        PreparedStatement statement = cnx.prepareStatement(sql);
        statement.setInt(1, chequeId);


        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            Cheque cheque = new Cheque();
            cheque.setId(resultSet.getInt("id"));
            cheque.setNum(resultSet.getInt("Num"));
            cheque.setNumcompte(resultSet.getInt("Numcompte"));
            cheque.setMontant(resultSet.getFloat("montant"));
            cheque.setDate(resultSet.getDate("date"));
            return cheque;
        } else {
            // Virement with the given ID not found
            return null;
        }
    }

    public Cheque findById(int id) throws SQLException {
        String sql = "SELECT * FROM Cheque WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cheque cheque = new Cheque();
                    cheque.setId(rs.getInt("id"));
                    cheque.setNum(rs.getInt("Num"));
                    cheque.setNumcompte(rs.getInt("Numcompte"));
                    cheque.setMontant(rs.getInt("montant"));
                    cheque.setDate(rs.getDate("date"));
                    return cheque;
                }
            }
        }
        return null; // Retourne null si aucun crédit n'est trouvé avec cet identifiant
    }
}


