package codes.pnk.controller;

import codes.pnk.model.domain.algorithm.Algorithm;
import codes.pnk.model.domain.common.Image;
import codes.pnk.model.domain.common.Text;
import codes.pnk.model.domain.exception.AlgorithmException;
import codes.pnk.model.domain.exception.ImageException;
import codes.pnk.model.domain.exception.TextException;
import codes.pnk.model.presentation.ViewActionType;
import codes.pnk.model.presentation.ViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AlgorithmController {
    private final Algorithm algorithm;

    public AlgorithmController(final Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void start(final ViewModel viewModel, final ViewActionType action) throws AlgorithmException {
        switch (action) {
            case EMBED -> embed(viewModel);
            default -> throw new AlgorithmException("Unsupported action type: " + action);
        }
    }

    private void embed(final ViewModel viewModel) throws AlgorithmException {
        try {
            final Text secret = new Text(viewModel.getTextFile().getValue());
            final Image cover = new Image(viewModel.getImageFile().getValue());
            final byte[] data = algorithm.embed(secret, cover);
            final File outputFile = viewModel.getOutputPath().getValue().resolve(cover.getName()).toFile();
            saveBytesToFile(data, outputFile);
        } catch (TextException e) {
            throw new AlgorithmException("Unable to resolve secret text file: " + e.getMessage());
        } catch (ImageException e) {
            throw new AlgorithmException("Unable to resolve image cover file: " + e.getMessage());
        }
    }

    private void saveBytesToFile(byte[] data, File file) throws AlgorithmException {
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(data);
        } catch (IOException e) {
            throw new AlgorithmException(e.getMessage());
        }
    }
}
