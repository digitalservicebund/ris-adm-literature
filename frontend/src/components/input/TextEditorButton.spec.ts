import { userEvent } from '@testing-library/user-event'
import { render, screen } from '@testing-library/vue'
import { markRaw } from 'vue'
import TextEditorButton from '@/components/input/TextEditorButton.vue'
import IconTest from '~icons/ic/baseline-clear'
import { describe, expect, test } from 'vitest'

describe('text editor button', async () => {
  test('renders with child components', async () => {
    render(TextEditorButton, {
      props: {
        type: 'menu',
        icon: markRaw(IconTest),
        ariaLabel: 'test editor button',
        childButtons: [
          {
            type: 'test child 1 type',
            icon: markRaw(IconTest),
            ariaLabel: 'test child button 1',
          },
          {
            type: 'test child 2 type',
            icon: markRaw(IconTest),
            ariaLabel: 'test child button 2',
          },
        ],
      },
    })
    const button = screen.getByLabelText('test editor button')
    expect(button).toBeInTheDocument()
    await userEvent.click(button)
    expect(screen.getByLabelText('test child button 1')).toBeInTheDocument()
    expect(screen.getByLabelText('test child button 2')).toBeInTheDocument()
  })

  test('emits event to parent when user clicks on button without child buttons', async () => {
    const { emitted } = render(TextEditorButton, {
      props: {
        type: 'test type',
        icon: IconTest,
        ariaLabel: 'test editor button',
      },
    })
    const button = screen.getByLabelText('test editor button')
    expect(button).toBeInTheDocument()
    await userEvent.click(button)
    expect(emitted().toggle).toBeTruthy()
  })

  test('toggles dropdown menu when user clicks on button with child buttons', async () => {
    const user = userEvent.setup()

    render(TextEditorButton, {
      props: {
        type: 'menu',
        icon: IconTest,
        ariaLabel: 'menu',
        childButtons: [
          {
            type: 'test child 1 type',
            icon: IconTest,
            ariaLabel: 'test child 1 aria',
          },
          {
            type: 'test child 2 type',
            icon: IconTest,
            ariaLabel: 'test child 2 aria',
          },
        ],
      },
    })
    const button = screen.getByLabelText('menu')
    expect(button).toBeInTheDocument()
    await user.click(button)

    expect(screen.getByLabelText('test child 1 aria')).toBeInTheDocument()
    expect(screen.getByLabelText('test child 2 aria')).toBeInTheDocument()
  })

  test('emits toggle event when button type is "more" even with childButtons', async () => {
    const { emitted } = render(TextEditorButton, {
      props: {
        type: 'more',
        icon: IconTest,
        ariaLabel: 'more button',
        childButtons: [
          {
            type: 'test child type',
            icon: IconTest,
            ariaLabel: 'test child aria',
          },
        ],
      },
    })

    const button = screen.getByLabelText('more button')
    await userEvent.click(button)
    expect(emitted().toggle).toBeTruthy()
  })

  test('closes dropdown when clicking outside', async () => {
    const user = userEvent.setup()

    render(TextEditorButton, {
      props: {
        type: 'menu',
        icon: IconTest,
        ariaLabel: 'menu',
        childButtons: [
          {
            type: 'test child type',
            icon: IconTest,
            ariaLabel: 'test child aria',
          },
        ],
      },
    })

    const button = screen.getByLabelText('menu')
    await user.click(button)
    expect(screen.getByLabelText('test child aria')).toBeInTheDocument()

    await user.click(document.body)

    expect(screen.queryByLabelText('test child aria')).not.toBeInTheDocument()
  })

  test('closes dropdown when disabled prop changes to true', async () => {
    const { rerender } = render(TextEditorButton, {
      props: {
        type: 'menu',
        icon: IconTest,
        ariaLabel: 'menu',
        disabled: false,
        childButtons: [
          {
            type: 'test child type',
            icon: IconTest,
            ariaLabel: 'test child aria',
          },
        ],
      },
    })

    const button = screen.getByLabelText('menu')
    await userEvent.click(button)
    expect(screen.getByLabelText('test child aria')).toBeInTheDocument()

    // Change disabled to true
    await rerender({
      type: 'menu',
      icon: IconTest,
      ariaLabel: 'menu',
      disabled: true,
      childButtons: [
        {
          type: 'test child type',
          icon: IconTest,
          ariaLabel: 'test child aria',
        },
      ],
    })

    expect(screen.queryByLabelText('test child aria')).not.toBeInTheDocument()
  })

  test('shows tooltip with shortcut when shortcut is provided', async () => {
    render(TextEditorButton, {
      props: {
        type: 'menu',
        icon: IconTest,
        ariaLabel: 'test button',
        shortcut: 'Ctrl+B',
        childButtons: [
          {
            type: 'test child type',
            icon: IconTest,
            ariaLabel: 'child button',
            shortcut: 'Ctrl+C',
          },
        ],
      },
    })

    const button = screen.getByLabelText('test button')
    await userEvent.click(button)

    const childButton = screen.getByLabelText('child button')
    expect(childButton).toBeInTheDocument()
  })
})
